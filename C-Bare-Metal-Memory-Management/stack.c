#include <stdio.h>
#include <stdlib.h>

void write_to_stack(void*, void*, unsigned short);
struct Stack_item read_from_stack(void*);

struct Stack {
	void *top;
	void *next;
	unsigned int stack_size;
	void *stack;
};

struct Stack_item {
	void *ptr;
	char type;
	unsigned short size;
	void *dat;
};

//" Put a new value on the stack and return the next available position
void push(struct Stack *stack, void *ptr, char type, unsigned short size) {

	unsigned int next = 11 + (unsigned int)size;
	unsigned long nextpos = (unsigned long)stack->next;
	unsigned long basepos = (unsigned long)stack->stack;
	unsigned long current_stack = nextpos - basepos;

	if (current_stack + next >= stack->stack_size) {
		printf("Stack overflow!\n");
		return;
	}

	unsigned long addr = (unsigned long)stack->top;

	write_to_stack(stack->next, (void*)&addr, 8);
	write_to_stack(stack->next + 8, &type, sizeof(char));
	write_to_stack(stack->next + 9, &size, sizeof(unsigned short));
	write_to_stack(stack->next + 11, ptr, size);

	stack->top = stack->next;
	stack->next = stack->top + next;
}

//" Remove the top item from the stack and return the next item's position
void pop(struct Stack *stack) {

	if (stack->top != stack->next) {
		struct Stack_item si = read_from_stack(stack->top);
		stack->next = stack->top;
		stack->top = si.ptr;
	} else {
		printf("Stack is empty, nothing to pop!\n");
	}
}

//" Preview the raw stack top item
void peek(struct Stack *stack) {

	if (stack->top == stack->next)
		return;

	struct Stack_item si = read_from_stack(stack->top);
	printf("Datatype: %c\n", si.type);
	printf("Size: %d\n", si.size);
	printf("Raw readout:");
	for (unsigned short i = 0; i < si.size; i++) {
		printf(" %x", *(char*)(si.dat + i));
	}
	printf("\n");
}

//" Preview the top stack item rendered in its data-type
void peek_render(struct Stack *stack) {

	if (stack->top == stack->next)
		return;

	struct Stack_item si = read_from_stack(stack->top);
	printf("Datatype: %c\n", si.type);
	printf("Size: %d\n", si.size);
	printf("Data: ");

	switch (si.type) {
		case 'c':
			printf("%c\n", *(char*)si.dat);
			break;
		case 's':
			printf("%d\n", *(short*)si.dat);
			break;
		case 'i':
			printf("%d\n", *(int*)si.dat);
			break;
		case 'l':
			printf("%d\n", *(long*)si.dat);
			break;
		case 'f':
			printf("%f\n", *(float*)si.dat);
			break;
		case 'd':
			printf("%f\n", *(double*)si.dat);
			break;
		default:
			printf("Type render broke\n");
			return;
	}
}

void write_to_stack(void *pos, void *dat, unsigned short size) {

	unsigned short i = 0;
	for (i = 0; i < size; i++) {
		*(char*)(pos + i) = *(char*)(dat + i);
	}
}

struct Stack_item read_from_stack(void *pos) {

	unsigned short i = 0;
	unsigned short data_offset = 11; // 8 byte pointer, 1 byte char, 2 byte short
	struct Stack_item si;
	char *ptr = malloc(8 * sizeof(char));

	for (i = 0; i < 8; i++) {
		*(ptr + i) = *(char*)(pos + i);
	}
	si.ptr = (void*)(*(long*)ptr);
	si.type = *(char*)(pos + 8);
	char *shrt = malloc(2 * sizeof(char));
	*(shrt + 0) = *(char*)(pos + 9); *(shrt + 1) = *(char*)(pos + 10);
	si.size = *(unsigned short*)shrt;
	si.dat = pos + 11;

	free(ptr); free(shrt);

	return si;
}

void print_stack(struct Stack *stack) {

	printf("Stack base address: %p and Max address: %p\n", stack->stack,
			stack->stack + stack->stack_size);
	printf("Stack max size %d\n", stack->stack_size);
	printf("Stack top address: %p\n", stack->top);
	printf("Stack next address: %p\n", stack->next);
}


int main(int argc, char *argv[]) {

	unsigned int s_size = 32000;
	struct Stack st;
	st.stack_size = s_size;
	st.stack = malloc(st.stack_size * sizeof(char));
	st.top = st.stack;
	st.next = st.stack;

	char letter = 'a';
	short _2byte = 100;
	int _4byte = 1000;
	long _8byte = 10000;
	float fpnum = 5.876f;
	double pi = 3.14f;

	print_stack(&st);

	push(&st, &letter, 'c', sizeof(char));

	print_stack(&st);

	peek(&st);
	peek_render(&st);
	pop(&st);
	pop(&st);

	print_stack(&st);

	push(&st, &letter, 'c', sizeof(char));
	push(&st, &_2byte, 's', sizeof(short));
	push(&st, &_4byte, 'i', sizeof(int));
	push(&st, &_8byte, 'l', sizeof(long));
	push(&st, &fpnum, 'f', sizeof(float));
	push(&st, &pi, 'd', sizeof(double));

	print_stack(&st);

	for (unsigned short i = 0; i < 6; i++) {
		peek(&st);
		peek_render(&st);
		pop(&st);
	}

	print_stack(&st);

	for (unsigned short i = 0; i < 1685; i++) {
		push(&st, &_8byte, 'l', sizeof(long));
	}

	print_stack(&st);

	free(st.stack);

	return 0;
}
