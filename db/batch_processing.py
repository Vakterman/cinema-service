# This is a batch processing Python script

import csv
import psycopg2
import configparser
import sys
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT

class BatchProcessingConfiguration:
    def __init__(self, path_to_config):
        config = configparser.ConfigParser()
        config.sections()
        config.read(path_to_config)
        self.directory = config.get('db-config', 'data.directory')
        self.postgres_host = config.get('db-config','postgres.host')
        self.postgres_database = config.get('db-config', 'postgres.database')
        self.postgres_login = config.get('db-config', 'postgres.login')
        self.postgres_password = config.get('db-config', 'postgres.password')
        self.init_script = config.get('db-config', 'postgres.init.script')

class PgInit:

    def __init__(self, config):

        conn = psycopg2.connect(dbname="postgres",
                                     user=config.postgres_login,
                                     password=config.postgres_password,
                                     host=config.postgres_host)
        conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
        conn.cursor().execute(f"DROP DATABASE IF EXISTS {config.postgres_database}")
        conn.cursor().execute(f"CREATE DATABASE {config.postgres_database}")
        conn.close()

        conn = psycopg2.connect(dbname=config.postgres_database,
                                             user=config.postgres_login,
                                             password=config.postgres_password,
                                             host=config.postgres_host)

        with open(config.init_script) as sql_file:
            sql_as_string = sql_file.read()
            conn.cursor().execute(sql_as_string)
            conn.commit()
        conn.close()

class PGWriter:
    def __init__(self, dbname, host, user, password):
        self.conn = psycopg2.connect(dbname=dbname, user=user,
                                     password=password, host=host)

    def process_row(self, entity):
        pass

    def close_connection(self):
        self.conn.close()


class Title:
    def __init__(self,
                 title_alpha_id,
                 primary_title,
                 original_title,
                 start_year,
                 end_year,
                 is_adult,
                 title_type,
                 runtime_minutes,
                 genre_list,
                 rating):
        self.title_alpha_id = title_alpha_id
        self.primary_title = primary_title
        self.original_title = original_title
        self.start_year = start_year
        self.end_year = end_year
        self.is_adult = is_adult
        self.title_type = title_type
        self.runtime_minutes = runtime_minutes
        self.genre_list = genre_list
        self.rating = rating
        self.title_type_id = 0

    def to_record(self):
        return (self.primary_title,
                self.original_title,
                self.start_year,
                self.end_year,
                self.is_adult,
                self.title_type_id,
                self.runtime_minutes,
                ",".join(self.genre_list),
                self.rating)


class TitleRating:
    def __init__(self, title_alpha_id, rating):
        self.title_alpha_id = title_alpha_id
        self.rating = rating


class PGWriterTitle(PGWriter):
    insert_title_query = """ INSERT INTO title_info.titles (primary_title, original_title,
                           start_year, end_year, is_adult, title_type_id, runtime_minutes, genres, rating)
                           VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s) RETURNING title_id"""

    insert_genre_query = """ INSERT INTO title_info.genres (genre_name) VALUES(%s) RETURNING genre_id"""
    insert_title_genres_connection = """ INSERT INTO title_info.title_genres (title_id, genre_id) VALUES(%s,%s) """
    insert_title_type_query = """ INSERT INTO title_info.title_type (type) VALUES(%s) RETURNING title_type_id"""
    insert_connection_title_genre = """ INSERT INTO title_info.title_genres (title_id, genre_id) VALUES(%s,%s)"""

    def __init__(self, dbname, host, user, password):
        super().__init__(dbname, host, user, password)
        self.title_ids = {}
        self.genre_ids = {}
        self.title_type_ids = {}

    def process_row(self, entity):
        self.conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
        cursor = self.conn.cursor()
        try:

            if entity.title_type not in self.title_type_ids:
                cursor.execute(self.insert_title_type_query, (entity.title_type,))
                self.conn.commit()
                title_type_id = cursor.fetchone()[0]
                self.title_type_ids[entity.title_type] = title_type_id
                entity.title_type_id = title_type_id
            else:
                entity.title_type_id = self.title_type_ids[entity.title_type]

            for genre in entity.genre_list:
                if genre not in self.genre_ids:
                    cursor.execute(self.insert_genre_query, (genre,))
                    self.conn.commit()
                    genre_id = cursor.fetchone()[0]
                    self.genre_ids[genre] = genre_id

            cursor.execute(self.insert_title_query, entity.to_record())

            self.conn.commit()
            last_inserted_id = cursor.fetchone()[0]
            self.title_ids[entity.title_alpha_id] = last_inserted_id

            for genre in entity.genre_list:
                cursor.execute(self.insert_title_genres_connection, (last_inserted_id, self.genre_ids[genre]))
                self.conn.commit()

        except (Exception, psycopg2.Error) as err:
            print("Failed to insert record", err)
        finally:
            cursor.close()


class PGRatingWriter(PGWriter):
    update_rating_value = """ UPDATE title_info.titles SET rating = %s WHERE title_id = %s"""

    def __init__(self, title_ids, dbname, host, user, password):
        super().__init__(dbname, host, user, password)
        self.title_ids = title_ids

    def process_row(self, entity):
        cursor = self.conn.cursor()
        try:
            if entity.title_alpha_id in self.title_ids:
                title_id = self.title_ids[entity.title_alpha_id]
                cursor.execute(self.update_rating_value, (entity.rating, title_id))
            else:
                print(f"Id for identifier: {entity.title_alpha_id} not found")
        except (Exception, psycopg2.Error) as err:
            print("Failed to update raiting", err)
        finally:
            cursor.close()


class CSVProcessor:
    def __init__(self, fname, pg_writer):
        self.csv_file_name = fname
        self.pg_writer = pg_writer

    def process(self):
        num_lines = sum(1 for line in open(self.csv_file_name))
        print(f"Start processing: {self.csv_file_name}")
        with open(self.csv_file_name, mode='r') as csv_file:
            current_line = 0
            csv_reader = csv.DictReader(csv_file, delimiter='\t')
            for row in csv_reader:
                self.process_row(row)
                current_line += 1
                print(f"Progress {round(current_line / num_lines) * 100} ")

    def get_entity(self, row):
        return None

    def process_row(self, row):
        """ abstract method"""
        entity = self.get_entity(row)
        self.pg_writer.process_row(entity)
        pass


class TitleProcessor(CSVProcessor):
    def __init__(self, fname, pg_writer):
        super().__init__(fname, pg_writer)
        self.type_list = []

    def get_entity(self, row):
        end_year = row['endYear']
        if end_year == "\\N":
            end_year = 0
        else:
            end_year = int(row['endYear'])

        runtime_minutes = 0
        if row['runtimeMinutes'] != '\\N':
            runtime_minutes = int(row['runtimeMinutes'])
        genres = list(filter(lambda x: x != "\\N", row['genres'].split(',')))
        startYearStr = row['startYear']
        if startYearStr == "\\N":
            startYear = 0
        else:
            startYear = int(row["startYear"])

        return Title(row['tconst'],
                     row['primaryTitle'],
                     row['originalTitle'],
                     startYear,
                     end_year,
                     bool(row['isAdult']),
                     row['titleType'],
                     runtime_minutes,
                     genres,
                     0)


class RatingProcessor(CSVProcessor):
    def __init__(self, fname, pg_writer):
        super().__init__(fname, pg_writer)

    def get_entity(self, row):
        return TitleRating(row['tconst'], float(row['averageRating']))


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    if len(sys.argv) == 1:
        path_to_config = 'batch_processing.conf'
    else:
        path_to_config = sys.argv[1]

    configuration = BatchProcessingConfiguration(path_to_config)

    pg_init = PgInit(configuration)
    pg_writer = PGWriterTitle(configuration.postgres_database,
                              configuration.postgres_host,
                              configuration.postgres_login,
                              configuration.postgres_password)

    csv_processor = TitleProcessor(f'{configuration.directory}/title.basics.tsv', pg_writer)
    csv_processor.process()
    pg_writer.genre_ids.clear()
    pg_writer.title_type_ids.clear()

    pg_rating_writer = PGRatingWriter(pg_writer.title_ids, configuration.postgres_database, configuration.postgres_host, configuration.postgres_login, configuration.postgres_password)
    rating_processor = RatingProcessor(f'{configuration.directory}/title.ratings.tsv', pg_rating_writer)
    rating_processor.process()
    pg_rating_writer.title_ids.clear()
