	.equ SASO_ADDR, 0xFF40 ;Considerando que o periférico SASO_Timer_v3 deverá ser acessível na gama de endereços 0xFF40 a 0xFF7F
	

.section .startup 
	b start
 
start:
	mov sp, stack_top
	bl main
	b .
	
	.text
main:          
push lr

mov r0,8
bl timer_init  
bl getvalue

ldr r1, saso_addr_temp
bl loopgetvalue
b .

loopgetvalue: 				 ;uint8_t timer_get_value(void);               3 
	
	ldrb r0, [r1]
	b loopgetvalue

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
  
