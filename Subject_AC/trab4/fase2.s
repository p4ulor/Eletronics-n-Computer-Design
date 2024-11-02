	.equ SASO_ADDR, 0xFF40 ;Considerando que o periférico SASO_Timer_v3 deverá ser acessível na gama de endereços 0xFF40 a 0xFF7F
	.equ SDP16_PORTS_ADDRESS, 0xFF00
	.equ TIME_DELAY, 250

.section .startup 
	b start
	b isr
 
start:
	ldr sp, stack_top_addr
	b main
	
stack_top_addr:
	.word stack_top
	
	.text
main:  
;push lr
mov r0, 0b11111111
bl timer_init         ;timer_init(byte val) inicar o saso timer com 1 valor para comecar a contagem descrescente
b init               
	
init:
	bl getvalue
	mov r2, 0b10000000    ;get value subtitui r0 e r1
	and r0, r0, r2       ;mask most significant bit
	bl port_output		  ;acao no ponto, (piscar)
	bl button0            ;confirmar ativacao i0
	b init

button0: 				;PRESSIONAMENTO BOTOES  I0
	push lr
	mov r2, #1          ;r2->mask
	bl port_input		;r0->valor lido       
	and r0, r0, r2      ;r0 & mask -> valor para avaliar, ver se e um 0 ou 1
	mov r0,#0           ; spin(0) (sem ponto)
	bzc spin            ;if flag Z = 0, o botao esta pressionado, e vai para o spin
	pop lr				;se nao, volta para o ciclo, do ponto piscar
	mov pc,lr
button1:                ;I1
	push lr
	mov r2, #2
	bl port_input
	and r0, r0, r2      ;(r0->mask(um "adress")
	bzc storechoice     ;if flag Z = 0, o botao esta pressionado, e vai para o storechoice
	pop lr				;se nao, volta para o ciclo, de estar a rodar
	mov pc,lr
	
spin:					;spin(bit mode)  mode = 0, ponto desligado, mode = 0x80(1000 0000), ponto ligado
	mov r5,#1  
	mov r4,#64           ; vou de 00000001 para 00100000 com shifts -> 2^6 = 64
cycle:
	mov r6, r0          ;r6 tem o modo, o bit que define se e com ponto ou nao
	mov r1,#0            ;reset, apagar tudo, mais simples.
	or r0, r1, r6  	     ;para suportar, ou com o ponto ligado ou desligado
	bl port_output
	bl delay
	or r0, r5, r6         ;para suportar, ou com o ponto ligado ou desligado
	bl port_output		 ;acender o q é para acender, acender o proximo
	bl delay
	lsl r5,r5,#1         ;muito importante
	sub r0, r6, #0       ;ver se é spin com ponto ou nao
	mov r0,r6			 ;recuperar o modo
	bzc componto		 ;o estado em q ja tem ponto, e o estado em q ja carregamos no I1 anteriormente, por isso passamos a frente isto
	bl button1           ;ver se I1 esta carregado
	b semponto
componto:
	bzc enableisr
semponto:
	cmp r5,r4		     ;for(int i(r5)=1; i<64; i<<1) i<64(decimal) 
	bzs spin             ;recomecar o ciclo
	b cycle              ;o ciclo ainda nao acabou, continuar o ciclo

storechoice:
	bl port_input
	mov r1, 0b11110000  ;mask i7-i4
	and r0, r0, r1
	lsr r0, r0, #4
	ldr r1, chosen_addr
	strb r0, [r1]
	bl getvalue					;gerar numero aleatorio
	lsr r0, r0, #4				;apropriar para ser 4 bits
	ldr r1, random_addr
	strb r0,[r1]							
	mov r0,#128 				;continuar a fazer spin, mas com ponto agora
	b semponto           	 	;continuar o ciclo de onde estava, chama se "semponto" mas este é o sitio para voltar 
	
chosen_addr:
	.word chosen

delay:
	push lr
	mov r1, TIME_DELAY
	;mov r1, TIME_DELAY & 0xff 	;se for uma valor mais elevado, usa-se isto
	;movt r1, TIME_DELAY >> 8
whiledelay:
	bzs whiledelay_end
	sub r1, r1, #1 				; && --delay_counter > 0
	bzc whiledelay
whiledelay_end:
	pop lr
	mov pc, lr
	
enableisr:          			;(IRQ)    1khz -> 0.001s, 8bits, 2^8-1=255,255 * 0.001 = 0.255s, 5s/0.255s = 19.5 -> 20 contagens; 5s-10s = 20-40ticks
	mrs r12, cpsr				;3ciclos
	mov r0, 0b10000 			;3c
	msr cpsr,r0					;automaticamente atualiza LR para a instrucao a seguir desta, aqui as flags podem ser alteradas; 3c
	msr cpsr, r12				;recoperamos as flags
	mov r0,#128					;continuar a fazer spin, mas com ponto agora
	b semponto					;faço branch para "semponto" mas é só pq assim é mais facil para voltar de onde estava
endisr:							;end bet
	msr cpsr, r12
	mov r3,#20
	ldr r2, waittime_addrs
	str r3,[r2]	     			;reset variavel tempo, contagem de tempo, 5s, 5s->20ticks
	b draw
isr:
	push lr
	ldr r1, waittime_addrs
	ldr r3,[r1]
getagain:
	bl getvalue           		;geracao numero aleatiorio, 
	mov r2, 0b00010101     		;21
	mov r1, 0b00011111
	and r0,r0,r1				;random val entre 0-20 ticks, que faz variar um valor X entre 20-40 ticks(5s-10s)
	cmp r0,r2					;random val->r0, entre 0 e 20
	bhs getagain				;confirmar q é menor que 21				         
	add r0,r3,r0           		;random range
	blt endisr					;se a soma anterior deu numero negativo, salta, salta se N = true
	;se o valor < 0, é pq fomos de 5s + (valor aleatorio neste instante) até valor inferior a 0, logo concluimos a contagem de pelo menos 5s mais um valor aleatorio(e q nao é superior a 5s), a menos q 0, logo terminamos a contagem aleatoria
	sub r3,r3,#1          		;dec do fixo 
	ldr r1, waittime_addrs
	str r3,[r1]	     			;store do fixo decrmentado
	pop lr
	mov pc, lr
	
waittime_addrs:
	.word waitime
	
draw:							;draw(numero q calhou) numero que foi sorteado na roleta
	ldr r0, random_addr
	ldrb r5,[r0]				;r5->numero q calhou	 
	ldr r1, addr_array			;base
	add r1,r1,r5 				;soma para escolher o o correspondente
	ldrb r0, [r1]				;shape do numero que saiu
	ldr r1, chosen_addrr
	ldrb r3, [r1]				;numero escolhido
	cmp r3,r5
	beq correct
	b incorrect
random_addr:
	.word randomnumber
correct:						;menor no shape um ponto se ta certo, sem ponto se esta incorreto 
	mov r1,#128
	or r0,r0,r1
	bl port_output
	b restart
incorrect:
	bl port_output
	b restart
restart:
	mov r2, #2          ;r2->mask
	bl port_input		;r0->valor lido       
	and r0, r0, r2      ;r0 & mask -> valor para avaliar, ver se e um 0 ou 1
	bzc init            ;if flag Z = 0, o botao esta pressionado, e vai para o init, para recomecar o jogo
	b restart
	
chosen_addrr:
	.word chosen
	
getvalue: 				 ;uint8_t timer_get_value(void);         read value do timer     
	ldr r1, saso_addr_temp
	ldrb r0, [r1]
	mov pc, lr

timer_init:				 ;void timer_init(uint8_t interval)     write value para o timer. 5, iniciar o LR, para comecar a contar de 1 certo valor
	ldr r1, saso_addr_temp
	strb r0,[r1]
	mov pc,lr
	
saso_addr_temp:  												
	.word	SASO_ADDR
	
port_input:					;ler do port					;PORT OPERATIONS
	ldr r1, port_addr
	ldrb r0, [r1]
	mov pc, lr
port_output:				;escrever
	ldr r1, port_addr
	strb r0, [r1]
	mov pc, lr
	
port_addr:
.word SDP16_PORTS_ADDRESS

.data
array_shape_numb:
	.byte 0x3F, 0x06, 0x5B, 0x4F, 0x66, 0x6D, 0x7D, 0x07, 0x7F, 0x6F, 0x77, 0x7C, 0x39, 0x5E, 0x79, 0x71
	;      0      1     2     3     4     5    6      7    8      9    A      B     C     D     E     F
array_shape_numb_end:

addr_array:
	.word array_shape_numb
waitime:
	.word 20 		;inicializar a contagem do intervalo aleatorio apartir de 5s(20 ticks), 
chosen:
	.byte 0
randomnumber:
	.byte 0
	
	.section .stack 
	.space 4096
stack_top:


  
