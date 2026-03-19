	.file	"laba1.c"
	.text
	.globl	sse
	.type	sse, @function
sse:
.LFB0:
	.cfi_startproc
	endbr64
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movq	%rdi, -24(%rbp)
	movq	%rsi, -32(%rbp)
	movq	%rdx, -40(%rbp)
	movq	-24(%rbp), %rax
	movq	-32(%rbp), %rdx
	movq	-40(%rbp), %rcx
#APP
# 6 "laba1.c" 1
	movups (%rax), %xmm0
movups (%rdx), %xmm1
mulps %xmm1, %xmm0
movups %xmm0, (%rcx)

# 0 "" 2
#NO_APP
	movl	$0, -4(%rbp)
	jmp	.L2
.L3:
	addl	$1, -4(%rbp)
.L2:
	cmpl	$3, -4(%rbp)
	jle	.L3
	nop
	nop
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE0:
	.size	sse, .-sse
	.globl	sequential
	.type	sequential, @function
sequential:
.LFB1:
	.cfi_startproc
	endbr64
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movq	%rdi, -24(%rbp)
	movq	%rsi, -32(%rbp)
	movq	%rdx, -40(%rbp)
	movl	$0, -4(%rbp)
	jmp	.L5
.L6:
	movl	-4(%rbp), %eax
	cltq
	leaq	0(,%rax,4), %rdx
	movq	-24(%rbp), %rax
	addq	%rdx, %rax
	movss	(%rax), %xmm1
	movl	-4(%rbp), %eax
	cltq
	leaq	0(,%rax,4), %rdx
	movq	-32(%rbp), %rax
	addq	%rdx, %rax
	movss	(%rax), %xmm0
	movl	-4(%rbp), %eax
	cltq
	leaq	0(,%rax,4), %rdx
	movq	-40(%rbp), %rax
	addq	%rdx, %rax
	mulss	%xmm1, %xmm0
	movss	%xmm0, (%rax)
	addl	$1, -4(%rbp)
.L5:
	cmpl	$3, -4(%rbp)
	jle	.L6
	nop
	nop
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE1:
	.size	sequential, .-sequential
	.section	.rodata
.LC5:
	.string	"\320\240\320\265\320\267\321\203\320\273\321\214\321\202\320\260\321\202 SSE: "
.LC6:
	.string	"%f "
.LC8:
	.string	"\n\320\222\321\200\320\265\320\274\321\217 SSE: %f \321\201\320\265\320\272\n"
	.align 8
.LC9:
	.string	"\320\240\320\265\320\267\321\203\320\273\321\214\321\202\320\260\321\202 \320\277\320\276\321\201\320\273\320\265\320\264\320\276\320\262\320\260\321\202\320\265\320\273\321\214\320\275\320\276\320\263\320\276 \321\203\320\274\320\275\320\276\320\266\320\265\320\275\320\270\321\217: "
	.align 8
.LC10:
	.string	"\n\320\222\321\200\320\265\320\274\321\217 \320\277\320\276\321\201\320\273\320\265\320\264\320\276\320\262\320\260\321\202\320\265\320\273\321\214\320\275\320\276\320\263\320\276: %f \321\201\320\265\320\272\n"
	.text
	.globl	main
	.type	main, @function
main:
.LFB2:
	.cfi_startproc
	endbr64
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$112, %rsp
	movq	%fs:40, %rax
	movq	%rax, -8(%rbp)
	xorl	%eax, %eax
	movss	.LC0(%rip), %xmm0
	movss	%xmm0, -64(%rbp)
	movss	.LC1(%rip), %xmm0
	movss	%xmm0, -60(%rbp)
	movss	.LC2(%rip), %xmm0
	movss	%xmm0, -56(%rbp)
	movss	.LC3(%rip), %xmm0
	movss	%xmm0, -52(%rbp)
	movss	.LC4(%rip), %xmm0
	movss	%xmm0, -48(%rbp)
	movss	.LC0(%rip), %xmm0
	movss	%xmm0, -44(%rbp)
	movss	.LC1(%rip), %xmm0
	movss	%xmm0, -40(%rbp)
	movss	.LC2(%rip), %xmm0
	movss	%xmm0, -36(%rbp)
	movl	$1000000, -84(%rbp)
	call	clock@PLT
	movq	%rax, -80(%rbp)
	movl	$0, -100(%rbp)
	jmp	.L8
.L9:
	leaq	-32(%rbp), %rdx
	leaq	-48(%rbp), %rcx
	leaq	-64(%rbp), %rax
	movq	%rcx, %rsi
	movq	%rax, %rdi
	call	sse
	addl	$1, -100(%rbp)
.L8:
	movl	-100(%rbp), %eax
	cmpl	-84(%rbp), %eax
	jl	.L9
	call	clock@PLT
	movq	%rax, -72(%rbp)
	leaq	.LC5(%rip), %rax
	movq	%rax, %rdi
	movl	$0, %eax
	call	printf@PLT
	movl	$0, -96(%rbp)
	jmp	.L10
.L11:
	movl	-96(%rbp), %eax
	cltq
	movss	-32(%rbp,%rax,4), %xmm0
	pxor	%xmm2, %xmm2
	cvtss2sd	%xmm0, %xmm2
	movq	%xmm2, %rax
	movq	%rax, %xmm0
	leaq	.LC6(%rip), %rax
	movq	%rax, %rdi
	movl	$1, %eax
	call	printf@PLT
	addl	$1, -96(%rbp)
.L10:
	cmpl	$3, -96(%rbp)
	jle	.L11
	movq	-72(%rbp), %rax
	subq	-80(%rbp), %rax
	pxor	%xmm0, %xmm0
	cvtsi2sdq	%rax, %xmm0
	movsd	.LC7(%rip), %xmm1
	divsd	%xmm1, %xmm0
	movq	%xmm0, %rax
	movq	%rax, %xmm0
	leaq	.LC8(%rip), %rax
	movq	%rax, %rdi
	movl	$1, %eax
	call	printf@PLT
	movl	$10, %edi
	call	putchar@PLT
	call	clock@PLT
	movq	%rax, -80(%rbp)
	movl	$0, -92(%rbp)
	jmp	.L12
.L13:
	leaq	-32(%rbp), %rdx
	leaq	-48(%rbp), %rcx
	leaq	-64(%rbp), %rax
	movq	%rcx, %rsi
	movq	%rax, %rdi
	call	sequential
	addl	$1, -92(%rbp)
.L12:
	movl	-92(%rbp), %eax
	cmpl	-84(%rbp), %eax
	jl	.L13
	call	clock@PLT
	movq	%rax, -72(%rbp)
	leaq	.LC9(%rip), %rax
	movq	%rax, %rdi
	movl	$0, %eax
	call	printf@PLT
	movl	$0, -88(%rbp)
	jmp	.L14
.L15:
	movl	-88(%rbp), %eax
	cltq
	movss	-32(%rbp,%rax,4), %xmm0
	pxor	%xmm3, %xmm3
	cvtss2sd	%xmm0, %xmm3
	movq	%xmm3, %rax
	movq	%rax, %xmm0
	leaq	.LC6(%rip), %rax
	movq	%rax, %rdi
	movl	$1, %eax
	call	printf@PLT
	addl	$1, -88(%rbp)
.L14:
	cmpl	$3, -88(%rbp)
	jle	.L15
	movq	-72(%rbp), %rax
	subq	-80(%rbp), %rax
	pxor	%xmm0, %xmm0
	cvtsi2sdq	%rax, %xmm0
	movsd	.LC7(%rip), %xmm1
	divsd	%xmm1, %xmm0
	movq	%xmm0, %rax
	movq	%rax, %xmm0
	leaq	.LC10(%rip), %rax
	movq	%rax, %rdi
	movl	$1, %eax
	call	printf@PLT
	movl	$0, %eax
	movq	-8(%rbp), %rdx
	subq	%fs:40, %rdx
	je	.L17
	call	__stack_chk_fail@PLT
.L17:
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE2:
	.size	main, .-main
	.section	.rodata
	.align 4
.LC0:
	.long	1075838976
	.align 4
.LC1:
	.long	1080033280
	.align 4
.LC2:
	.long	1083179008
	.align 4
.LC3:
	.long	1085276160
	.align 4
.LC4:
	.long	1069547520
	.align 8
.LC7:
	.long	0
	.long	1093567616
	.ident	"GCC: (Ubuntu 13.3.0-6ubuntu2~24.04.1) 13.3.0"
	.section	.note.GNU-stack,"",@progbits
	.section	.note.gnu.property,"a"
	.align 8
	.long	1f - 0f
	.long	4f - 1f
	.long	5
0:
	.string	"GNU"
1:
	.align 8
	.long	0xc0000002
	.long	3f - 2f
2:
	.long	0x3
3:
	.align 8
4:
