	.equ SASO_ADDR, 0xFF40 ;Considerando que o periférico SASO_Timer_v3 deverá ser acessível na gama de endereços 0xFF40 a 0xFF7F
	

.section .startup 
	b start
	b isr
 
start:
	mov sp, stack_top
	bl main
	b .
	
	.text
main:          
mov r0,8
bl timer_init  
bl enableisr
pop lr
mov pc, r0
b .

enableisr:          			
	mrs r12, cpsr				
	msr spsr, r12				;salvar flags
	mov r0, 0b10000 			;IE = 1
	msr cpsr,r0					;automaticamente atualiza LR para a instrucao a seguir desta, aqui as flags podem ser alteradas
	msr cpsr, r12				;recoperamos as flags, IE = 0 automaticamente				
	b enableisr	
	
isr:
	push lr
	bl getvalue
	pop lr
	mov pc, lr


getvalue: 				 ;uint8_t timer_get_value(void);               3 
	ldr r1, saso_addr_temp
	ldrb r0, [r1]
	mov pc, lr

	
timer_init:				 ;void timer_init(uint8_t interval)           5, iniciar o LR, para comecar a contar de 1 certo valor
	ldr r1, saso_addr_temp
	strb r0,[r1]
	mov pc,lr
	

saso_addr_temp:  												;tem q tar sempre a frente para ser usado
	.word	SASO_ADDR

	.data

	.section .stack 
	.space 64
stack_top:
  
