import java.io.*;
import java.net.*;

public class Servidor {

    public static void main(String[] args) {
        int porta = 5000; // Porta de comunicação do servidor
        try (ServerSocket servidorSocket = new ServerSocket(porta)) {
            System.out.println("Servidor iniciado na porta " + porta);

            while (true) {
                System.out.println("\nAguardando conexão de um cliente...");
                Socket clienteSocket = servidorSocket.accept(); // Espera conexão
                System.out.println("Cliente conectado: " + clienteSocket.getInetAddress());

                // Cria streams de entrada e saída
                DataInputStream entrada = new DataInputStream(clienteSocket.getInputStream());
                DataOutputStream saida = new DataOutputStream(clienteSocket.getOutputStream());

                // Lista arquivos disponíveis na pasta "arquivos"
                File pasta = new File("arquivos");
                File[] lista = pasta.listFiles();
                StringBuilder nomesArquivos = new StringBuilder();

                for (int i = 0; i < lista.length; i++) {
                    nomesArquivos.append((i + 1) + " - " + lista[i].getName() + "\n");
                }

                // Envia lista ao cliente
                saida.writeUTF("Arquivos disponíveis:\n" + nomesArquivos.toString());
                saida.writeUTF("Digite o nome do arquivo desejado:");

                // Recebe o nome do arquivo solicitado
                String nomeArquivo = entrada.readUTF();
                File arquivoSolicitado = new File(pasta, nomeArquivo);

                if (arquivoSolicitado.exists()) {
                    saida.writeUTF("ENCONTRADO");
                    long tamanho = arquivoSolicitado.length();
                    saida.writeLong(tamanho);

                    // Envia o arquivo em bytes
                    FileInputStream fis = new FileInputStream(arquivoSolicitado);
                    byte[] buffer = new byte[4096];
                    int bytesLidos;
                    while ((bytesLidos = fis.read(buffer)) != -1) {
                        saida.write(buffer, 0, bytesLidos);
                    }
                    fis.close();
                    System.out.println("Arquivo '" + nomeArquivo + "' enviado com sucesso!");
                } else {
                    saida.writeUTF("NAO_ENCONTRADO");
                    System.out.println("Arquivo não encontrado: " + nomeArquivo);
                }

                clienteSocket.close(); // Encerra a conexão com o cliente
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
