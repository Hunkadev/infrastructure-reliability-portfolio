// Add support for dynamic row/data sizes passed at runtime

#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>


struct Address {
	int id;
	int set;
	char *name;
	char *email;
};

struct Database {
	int num_rows;
	int max_data;
	struct Address *rows;
};

struct Connection {
	FILE *file;
	struct Database *db;
};

void Database_close(struct Connection *conn);

void die(const char *message, struct Connection *conn) {

	if (conn)
		Database_close(conn);

	if (errno) {
		perror(message);
	} else {
		printf("ERROR: %s\n", message);
	}

	exit(1);
}

void Address_print(struct Address *addr) {

	printf("%d %s %s\n", addr->id, addr->name, addr->email);
}

void Database_load(struct Connection *conn) {

	int rc = fread(&conn->db->num_rows, sizeof(int), 1, conn->file);
	if (rc != 1)
		die("Failed to read database size.", conn);

	rc = fread(&conn->db->max_data, sizeof(int), 1, conn->file);
	if (rc != 1)
		die("Failed to read max character length.", conn);

	conn->db->rows = malloc(conn->db->num_rows * sizeof(struct Address));

	if (conn->db->rows) {
		int i;
		int msize = conn->db->max_data;
		struct Address *addr;
		for (i = 0; i < conn->db->num_rows; i++) {
			addr = &conn->db->rows[i];
			rc = fread(&addr->id, sizeof(int), 1, conn->file);
			if (rc != 1)
				die("Failed to read id.", conn);
			rc = fread(&addr->set, sizeof(int), 1, conn->file);
			if (rc != 1)
				die("Failed to read set.", conn);
			addr->name = malloc(msize * sizeof(char));
			rc = fread(addr->name, msize * sizeof(char), 1, conn->file);
			if (rc != 1)
				die("Failed to read name.", conn);
			addr->email = malloc(msize * sizeof(char));
			rc = fread(addr->email, msize * sizeof(char), 1, conn->file);
			if (rc != 1)
				die("Failed to read email.", conn);
		}
	}
}

struct Connection *Database_open(const char *filename, char mode) {

	struct Connection *conn = malloc(sizeof(struct Connection));
	if (!conn)
		die("Memory error: connection", NULL);

	conn->db = malloc(sizeof(struct Database));
	if (!conn->db)
		die("Memory error: database", conn);

	if (mode == 'c') {
		conn->file = fopen(filename, "w");
	} else {
		conn->file = fopen(filename, "r+");

		if (conn->file) {
			Database_load(conn);
		} else {
			die("Unable to load database file", conn);
		}
	}

	return conn;
}

void Database_close(struct Connection *conn) {

	if (conn) {
		printf("Shutting down database.\n");
		if (conn->file)
			fclose(conn->file);
		if (conn->db) {
			if (conn->db->rows) {
				int i;
				int num_rows = conn->db->num_rows;
				struct Address *addr;
				for (i = 0; i < num_rows; i++) {
					addr = &conn->db->rows[i];
					free(addr->name);
					free(addr->email);
				}
				free(conn->db->rows);
			}
			free(conn->db);
		}
		free(conn);
	}
}

void Database_write(struct Connection *conn) {

	rewind(conn->file);

	int rc = fwrite(&conn->db->num_rows, sizeof(int), 1, conn->file);
	if (rc != 1)
		die("Failed to write number of rows.", conn);

	rc = fwrite(&conn->db->max_data, sizeof(int), 1, conn->file);
	if (rc != 1)
		die("Failed to write max chars.", conn);

	int i;
	int msize = conn->db->max_data;
	struct Address *addr;
	for (i = 0; i < conn->db->num_rows; i++) {
		addr = &conn->db->rows[i];
		rc = fwrite(&addr->id, sizeof(int), 1, conn->file);
		if (rc != 1)
			die("Failed to write id.", conn);
		rc = fwrite(&addr->set, sizeof(int), 1, conn->file);
		if (rc != 1)
			die("Failed to write set.", conn);
		rc = fwrite(addr->name, msize * sizeof(char), 1, conn->file);
		if (rc != 1)
			die("Failed to write name to file.", conn);
		rc = fwrite(addr->email, msize * sizeof(char), 1, conn->file);
		if (rc != 1)
			die("Failed to write email to file.", conn);
	}

	rc = fflush(conn->file);
	if (rc == -1)
		die("Cannot flush database.", conn);
}

void Database_create(struct Connection *conn, int rows, int data) {

	conn->db->num_rows = rows;
	conn->db->max_data = data;
	int i = 0;

	conn->db->rows = malloc(conn->db->num_rows * sizeof(struct Address));

	for (i = 0; i < conn->db->num_rows; i++) {
		// make a prototype to initialize it
		struct Address *addr = &conn->db->rows[i];
		addr->id = i;
		addr->set = 0;
		addr->name = malloc(conn->db->max_data * sizeof(char));
		addr->email = malloc(conn->db->max_data * sizeof(char));
	}
}

void Database_set(struct Connection *conn, int id, const char *name, const char *email) {

	struct Address *addr = &conn->db->rows[id];
	if (addr->set)
		die("Alredy set, delete it first.", conn);

	addr->set = 1;
	// WARNING: bug, read the "How To Break It" and fix this
	char *res = strncpy(addr->name, name, (conn->db->max_data)-1);
	addr->name[(conn->db->max_data)-1]='\0';
	// demonstrate the strncpy bug
	if (!res)
		die("Name copy failed", conn);

	res = strncpy(addr->email, email, (conn->db->max_data)-1);
	addr->name[(conn->db->max_data)-1]='\0';
	if (!res)
		die("Email copy failed", conn);
}

void Database_get(struct Connection *conn, int id) {

	struct Address *addr = &conn->db->rows[id];

	if (addr->set) {
		Address_print(addr);
	} else {
		die("ID is not set", conn);
	}
}

void Database_delete(struct Connection *conn, int id) {

	struct Address *addr = &conn->db->rows[id];
	if (addr->set) {
		free(addr->name);
		free(addr->email);
		struct Address tmp = {.id = id, .set = 0};
		addr = &tmp;
	} else {
		die("Address is not set, aborting delete.", conn);
	}
}

void Database_list(struct Connection *conn) {

	int i = 0;
	struct Database *db = conn->db;

	for (i = 0; i < conn->db->num_rows; i++) {
		struct Address *cur = &db->rows[i];

		if (cur->set) {
			Address_print(cur);
		}
	}
}

int validate_id(struct Connection *conn, char *cid) {
	int id = atoi(cid);
	if (id < 0) die("Invalid id, too low", conn);
	if (id >= conn->db->num_rows) die("Invalid id, too high", conn);

	return id;
}

// "MANY CONCERNS TO ADDRESS:"
// " 1) Use pointer lookup on initialized array of address pointers"
// "    ex. check if struct Address *addr = conn->db->addr[50]; addr->set = 1;
// "	SAFETY: CHECK IF ADDRESS POINTER IS NULL
//
// "Check first if a new database is being created or if an existing database
// "is being loaded. If a new database is being created, require the maximum
// "number of rows and the maximum data size for strings. If a database is being
// "loaded, get the database sizes from the file.

int main(int argc, char *argv[]) {

	if (argc < 3)
		die("USAGE: ex17 <dbfile> <action> [action params]", NULL);

	char *filename = argv[1];
	char action = argv[2][0];
	struct Connection *conn = Database_open(filename, action);
	int id = 0;

	switch (action) {
		case 'c':
			if (argc != 5)
				die("Need number of rows and max data size", conn);
			Database_create(conn, atoi(argv[3]), atoi(argv[4]));
			Database_write(conn);
			break;

		case 'g':
			if (argc != 4)
				die("Need an id to get", conn);

			id = validate_id(conn, argv[3]);

			Database_get(conn, id);
			break;

		case 's':
			if (argc != 6)
				die("Need id, name, email to set", conn);

			id = validate_id(conn, argv[3]);

			Database_set(conn, id, argv[4], argv[5]);
			Database_write(conn);
			break;

		case 'd':
			if (argc != 4)
				die("Need id to delete", conn);

			id = validate_id(conn, argv[3]);

			Database_delete(conn, id);
			Database_write(conn);
			break;

		case 'l':
			Database_list(conn);
			break;

		default:
			die("Invalid action: c=create, g=get, s=set, d=del, l=list",
					conn);
	}

	Database_close(conn);

	return 0;
}
