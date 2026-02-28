#include <stdio.h>

void print_byte(void *ptr) {

	for (int i = 128; i > 0; i = i/2) {
		if (*(char*)ptr & i) {
			printf("1");
		} else {
			printf("0");
		}
	}
	printf("\n");
}

void vararg(void *ptr, unsigned int size) {

	printf("Size of thing is %d\n", size);

	for (unsigned int i = 0; i < size; i++) {
		print_byte(ptr + i);
	}
}

int main(int argc, char *argv[]) {

	char c = 'a';
	short s = 1;
	int i = 2;
	long l = 3;
	float f = 1.3f;
	double d = 2.6f;

	struct inspect {
		int a;
		int b;
		char z;
		char y;
		double time_limit;
		char *name;
	};

	struct inspect i1 = {.a = 7, .b = 93, .z = 'z', .y = 'y', .name = "Charles"};


	printf("Passing char %c\n", c);
	vararg(&c, sizeof(c));

	c = c & 0;
	printf("Passing char %c\n", c);
	vararg(&c, sizeof(c));

	printf("Passing short %d\n", s);
	vararg(&s, sizeof(s));
	printf("Passing int %d\n", i);
	vararg(&i, sizeof(i));
	printf("Passing long %d\n", l);
	vararg(&l, sizeof(l));
	printf("Passing float %f\n", f);
	vararg(&f, sizeof(f));
	printf("Passing double %f\n", d);
	vararg(&d, sizeof(d));
	printf("Passing struct\n");
	vararg(&i1, sizeof(i1));

	return 0;
}
