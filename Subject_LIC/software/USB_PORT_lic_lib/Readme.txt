
SDK USB_PORT
Software para desenvolvimento com placa uLICx, disponibilizando dois portos (entrada/sa’da) atravŽs de USB num projeto Java em:

a) IntelliJ
Para usar o USBPORT num projeto Java, dever‡ seguir os seguintes passos:
Fazer download e descompactar o ficheiro USB_PORT.zip.
Copiar os ficheiros usbport.dll, presente na diretoria \x86 ou \amd64 consoante a plataforma, e usbport.jar para o direct—rio ra’z do projeto.
Testar o kit executando o usbport.jar com comando: java -jar usbport.jar numa janela de comandos.
No projecto dever‡ acrescentar o usbport.jar como um Arquivo/Jar externo. Fazendo: 
  File > Project Structure > Libraries > New Project Library
Para usar o kit (class UsbPort, etc.) fazer o import:import isel.leic.UsbPort.*;
Para usar as classes utilit‡rias (class Time, etc.) fazer import: import isel.leic.utils.*;

b) Eclipse

Para usar o USBPORT num projeto Java do eclipse, seguir os seguintes passos:
Fazer download e descompactar o ficheiro USB_PORT.zip.
Copiar os ficheiros usbport.dll, presente na diretoria \x86 ou \amd64 consoante a plataforma, e usbport.jar para o direct—rio ra’z do projeto.
Testar o kit executando o usbport.jar com o comando: java -jar usbport.jar numa janela de comandos. 
No projecto eclipse acrescentar ao "Build Path" o usbport.jar como um Arquivo/Jar externo. Fazendo: 
  Menu Project > Properties > Java Build Path > Libraries > Add External JARs...
Para usar o kit (class UsbPort, etc.) fazer o import: import isel.leic.UsbPort.*;
Para usar as classes utilit‡rias (class Time, etc.) fazer import: import isel.leic.utils.*;


ISEL, Pedro Miguens Matutino 2017