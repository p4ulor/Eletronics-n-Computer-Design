.equ WIDTH, 8	
.equ MAX_RESULTS, 3
.equ DIMAREA, MAX_RESULTS*2
.section .startup 
	b start
	b .
start:
	mov sp, stack_top_addr
	bl main
	b .
	
	.text
main: 
	push lr               ;vao ser chamados metodos q chamam metodos
	mov r4, addr_area     ;addr_area = r4 !!!
	ldr r0, addr_pol1  
	mov r1, #2
	bl	area		      ;area( pol1, 2 )  ro->resultado da chamada da funcao area ( bl faz salto e tambem faz antes mov lr, pc)
	strb r0, [r4,0]       ;areas[0]=area( pol1, 2 ); parte baixa                       (o array area é byte a byte)
	asr r0,r0,WIDTH       ;isolar parte alta, que vai para parte baixa
	strb r0, [r4,1]       ;guardar parte alta
	
	ldr r0, addr_pol2
	mov r1, #4
	bl	area		           
	strb r0, [r4,2]       ;areas[1] = area( pol2, 4 )
	asr r0,r0,WIDTH
	strb r0, [r4,3]

	ldr r0, addr_pol3
	mov r1, #4
	bl	area		          
	str r0, [r4,4]       ;areas[2] = area( pol2, 4 );
	asr r0,r0,WIDTH
	strb r0, [r4,5]
	
	mov r0,#0             ;return 0
	pop lr
	mov	pc, lr
	
stack_top_addr:
	.word stack_top
vertices_addr:            ;4.2 c)
	.word vertices
idx1_addr:
	.word idx1
idx2_adrr:
	.word idx2

	
addr_pol1:
	.word pol1
addr_pol2:
	.word pol2
addr_pol3:
	.word pol3
addr_area:
	.word areaarray

	
area:                     ; area( int8_t vertices[], uint8_t num_vert )      
	push lr				  ;				r0                 r1
	push r4
	push r5
	push r6
	push r7
	push r8
	
	mov r3,#3
	cmp r1,r3	  		  ;if( num_vert < 3 )
	blo iffalse_area      ;CORRIGI bhs PARA blo, salta se C = 1, se num_vert-3 da carry, entao numvert<3, logo a condicao e verdadiera e vai para o codigo de salto
	sub r8,r1,#1          ;num_vert - 1                                *     r8 =  num_vert - 1
	mov r4, #0            ;int16_t res = 0                                   r4 = res
	mov r5, #0			  ;uint16_t count  e  count = 0;                     r5 = count
	mov r6,r0             ;porque chamamos outro metodo que substitui r0*    r0 = r6 = vertices  
	
	;vertices -> r0 = r6  num_vert -> r1(q depois vai ser substituido)   r2=?  r3=3  res->r4  count->r5(q é incrementado)   r7=?  num_vert-1->r8   
for:
	cmp r5,r8             ;count < num_vert - 1
	bhs endfor_area		  ;salta se C = 0, logo se count-num_vert-1 dá C = 0 então é pq count>= num_vert-1, logo afirmaçao é falsa e salta            
	add r2,r5,#1          ;(count + 1)
	lsl r2,r2,#1		  ;(count + 1)*2									
	lsl r1,r5,#1		  ;count*2 
	mov r0,r6			  ; na 1º exec o "vertices" pode estar em r0, mas nas proximas iteracoes nao, no r0 estara o val retornado
	;                     vertices->r0     count*2->r1     (count+1)*2-> r2     r3=?  res->r4  count->r5  vertices->r6       r7=?    num_vert-1->r8            
	b partial_area        ;partial_area( vertices, count * 2 , ( count + 1 ) * 2 )   
	;agora r0 foi substituido pela chamada deste metodo, q tem o valor q queremos e r1,r2,r3, -> foram modificados!
	add r4,r4,r0          ;res += partial_area(...) valor retornado esta em r0
	add r5,r5,#1		  ;++count
	b for
endfor_area:	
	mov	r0,r6			  ;obter em r0 o vertices(q foi guardado em r6)
	mov r1,r8             ;obter em r1 o num_vert-1. Copiar para r1 o conteudo de r8  (q foi guardado em r8)
	mov r2,#0			  ;obter em r2 o valor 0
	b partial_area		  ;             partial_area( vertices, (num_vert – 1) * 2 , 0 ) -> r0 
	add r0,r4,r0          ;res = res +  '                      '                       '      res->r0
	b return
	
iffalse_area:				  ;return -1
	mov r0,#0
	sub r0,r0,#1
	b return
	
return:
	pop r8
	pop r7
	pop r6
	pop r5
	pop r4
	pop lr
	mov pc,lr
	

partial_area:            ; ( int8_t vertices[], uint16_t idx1, uint16_t idx2 )       4.2 b) Ocupa 36 bytes (numero de instrucoes * 2)
	push lr				 ;         r0		  		 r1				r2				          2bytes  3ciclos				
	push r4				 ;		 															  2b      3c
	push r5				 ;		  															  2b      3c
	
	ldrb r3, [r0, r2]    ;vertices[ idx2 ]     ldrb porque  V                                 2b  	  6c afinal?
	ldrb r4, [r0, r1]    ;vertices[ idx1 ] 		         int8_t vertices                      2b      6c afinal?
	sub r5,r3,r4	     ;int8_t width = vertices[ idx2 ] - vertices[ idx1 ];                 2b      3c
	add r1,r1,#1         ;idx1+1                                                              2b      3c
	add r2,r2,#1         ;idx2+1                                                              2b      3c
	ldrb r1, [r0, r1]    ;vertices[idx1+1]													  2b      6c afinal?
	ldrb r2, [r0, r2]    ;vertices[idx2+1]                                                    2b      6c afinal?
	add r1,r1,r2		 ;vertices[idx1+1] + vertices[idx2+1] 
	asr r1,r1,#1         ;         '          '        '     /2 para este exercicio assumui-se q as posicoes x e y sao positivas  2b  3c
	;                    r5->width
	mov r0,r5                                                                                ;2b      3c
	;                    r0->width r1->heigth 
	
	bl  multiply         ;return multiply( width, height ), resultado esta no r0  		      2b     3c
	pop r5																					 ;2b     3c
	pop r4																					 ;2b     3c
	pop lr																					 ;2b     3c
	mov	pc, lr																				 ;2b     3c
	
	;4.2 c) ciclos de relogio gastos na execução da função desenvolvida (excluindo a função multiply):54  (3*numero instrucoes, 3 porque: preparacao de endereco, leitura da instrucao e descodificacaoo da instrucao
	

multiply:				 ;multiply ( int8_t M, int8_t m ) 4.1        
	push r4				 ;                 r0         r1
	push r5
	push r6
	push r7
	push r8
	;																									     15    8 7       0
	lsl r4,r0,WIDTH		 ; a,M,WIDTH                                    r4 = a        a = M << WIDTH      a = |  M  |0000 0000|
	mov r5, #0           ;                                              r5 = 0
	sub r0,r5,r0 ; -M 	 ; 0-M = -M                                     r0 = -M                             15     8 7       0
	lsl r0, r0, WIDTH    ; lsl s,-M,WIDTH    s = (-M) << WIDTH          r0 = s   (nao se usa mais o M)   s = |  -M  |0000 0000|
	
	mov r5, 0b11111111 	 ; 0xFF	CORRECAO!!!!!!!!				        r5 = 0xFF
	and r5,r1,r5         ;int16_t p = m & 0xFF;                         r5 = p
	mov r1,#0 			 ;int8_t p_1 = 0                                r1 = 0 (nao se usa mais o m)
	
	mov r8, #0			 ;											    r8 = 0   constante
	mov r6, #1           ;                                              r6 = 0x1 constante
	mov r3, WIDTH
	mov r2, #0           ;uint8_t i = 0
	;s->r0    p_1->r1     i->r2    WIDTH->r3   a->r4     p->r5      0x1->r6      ?->r7    0->r8  

formulti:
	cmp r2,r3
	beq end_for          ;i < WIDTH, se i-WIDTH deu Z = 1, então i e igual a WIDTH, e nesse caso, salta. CORRECAO!!!!!!!!!!!!!!!!!!! para beq
	
	;if
	and r7,r5,r6         ;(p & 0x1) == 1                                   r7 = resultado do AND
	bne else_if2         ;se Z É 0, o resultado nao deu zero! logo, os valores NAO sao iguais, e salta
	;&&
	cmp r1,r6            ;p_1 == 1       se r1-1 !=0     Z = 0 
	bne inc     
	add r5,r5,r4         ;p = p+a
	b inc
	;                     EXPLICAÇAO - se 1º condiçao do 1º if é falsa, entao so vai ao if_else e ver apenas a segunda condição
	;                     se a 1º condiçao do 1º if é verdadeira entao ve-se a segunda condiçao(e depois seja esta V ou F)e salta-se o else_if depois
else_if2:
	cmp r1,r8            ;p_1 == 0
	bne inc
	add r5,r5,r0		;p += s; CORRECAO!!!!!!!!!!!!
	b inc
inc:
	and r1,r5,r6         ;p_1 = p & 0x1; 
	asr r5,r5,#1         ;p >>= 1              asr para o sinal se manter
	add r2,r2,#1         ;i++
	b formulti
	
end_for:
    mov r0,r5 			 ;return p
	pop r8
	pop r7
	pop r6
	pop r5
	pop r4
	mov	pc, lr 		     ;volta
	
	
	.data          
M:
	.byte 3
m:
	.byte 4
	
	
pol1:
	.byte 10, 10, 20, 20
pol2:
	.byte 0, 5, 10, -5, 0, -15, -10, -5
pol3:
	.byte 10, 0, 20, 10, 30, 0, 20, -10 
areaarray:
	.space DIMAREA     ;dando exemplo como no enunciado em que está "#define MAX_RESULTS 3" reserva-se 6 bytes, iniciados a 0, porque area e um array de 16 bits, logo é a dobrar os bytes.fiz assim pq se a constante MAX_RESULTS for alterada isto altera tb, nao preciso de vir aqui alterar tambem...


vertices:
	.byte 3,4,5,6       ;4.2 c)   (3,4) (5,6)
idx1:
	.word 0
idx2:
	.word 2

	.section .stack 
stack:
	.space 1024
stack_top:
  
