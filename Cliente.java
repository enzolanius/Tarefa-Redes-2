import java.io.*;
import java.net.*;

public class Cliente {

    public static void main(String[] args) {
        String servidor = "localhost"; // IP do servidor
        int porta = 5000;

        try (Socket socket = new Socket(servidor, porta)) {
            System.out.println("Conectado ao servidor.");

            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            // Recebe e mostra lista de arquivos
            System.out.println(entrada.readUTF());
            System.out.print(entrada.readUTF() + " ");
            String nomeArquivo = teclado.readLine();

            // Envia o nome do arquivo ao servidor
            saida.writeUTF(nomeArquivo);

            // Aguarda resposta
            String resposta = entrada.readUTF();
            if (resposta.equals("ENCONTRADO")) {
                long tamanho = entrada.readLong();
                FileOutputStream fos = new FileOutputStream("recebido_" + nomeArquivo);
                byte[] buffer = new byte[4096];
                int bytesLidos;
                long bytesRestantes = tamanho;

                while (bytesRestantes > 0 && (bytesLidos = entrada.read(buffer, 0, (int) Math.min(buffer.length, bytesRestantes))) != -1) {
                    fos.write(buffer, 0, bytesLidos);
                    bytesRestantes -= bytesLidos;
                }
                fos.close();
                System.out.println("Arquivo '" + nomeArquivo + "' recebido com sucesso!");
            } else {
                System.out.println("Arquivo não encontrado no servidor.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
