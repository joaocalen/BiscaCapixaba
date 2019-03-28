/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rede;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Particular
 */
public abstract class Comunicador {

    public static final int ESPERANDO_CONFIRMACAO = 1;
    public static final int ESPERANDO_JOGADORES = 2;
    public static final int ESPERANDO_INICIO = 3;
    public static final int ESPERANDO_DISTRIBUICAO = 4;
    public static final int ESPERANDO_JOGADA = 5;
    public static int status;
    public static final String PEDIR_CONEXAO = "Pedir conexao";
    public static final String CONFIRMACAO_1 = "Confirmado";
    public static final String CONFIRMACAO_2 = "Ja conectado";
    public static final String CONFIRMACAO_3 = "Numero de jogadores completo";

    protected static DatagramSocket socket;

    public Comunicador() {
        try {
            inicializarSocket();
        } catch (SocketException e) {
            JOptionPane.showMessageDialog(null, "Erro ao criar socket.");
        }
    }

    abstract void inicializarSocket() throws SocketException;

    public static void enviarPacote(String mensagem, String ipDestinatario, int porta)
            throws UnknownHostException, IOException {
        byte[] dados = mensagem.getBytes();
        try {
            InetAddress destinatario = InetAddress.getByName(ipDestinatario);
            DatagramPacket pacoteParaEnviar = new DatagramPacket(dados, dados.length, destinatario, porta);
            socket.send(pacoteParaEnviar);
            System.out.println("Pacote enviado");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao enviar pacote");
        }
    }

    public static void receberPacote() {
        while (true) {
            try {
                byte[] dados = new byte[100];
                final DatagramPacket pacoteRecebido = new DatagramPacket(dados, dados.length);
                socket.receive(pacoteRecebido);
                System.out.println("Pacote recebido");
                if (pacoteRecebido.getAddress().getHostAddress() == null
                        ? IniciarPartidaEmRede.obterEnderecoLocal() != null
                        : !pacoteRecebido.getAddress().getHostAddress().equals(IniciarPartidaEmRede.obterEnderecoLocal())) {

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            status = descobrirStatus(pacoteRecebido);
                            System.out.println(status);
                            try {
                                tratarPacote(pacoteRecebido, status);
                            } catch (IOException ex) {
                                Logger.getLogger(Comunicador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    t.start();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erro ao receber pacote.");
            }
        }
    }

    private static int descobrirStatus(DatagramPacket pacoteStatus) {
        int statusMensagem;

        String mensagemPacote = new String(pacoteStatus.getData(), 0, pacoteStatus.getLength());
        System.out.println(mensagemPacote);

        switch (mensagemPacote) {
            case CONFIRMACAO_1:
                statusMensagem = ESPERANDO_CONFIRMACAO;
                break;
            case CONFIRMACAO_2:
                statusMensagem = ESPERANDO_CONFIRMACAO;
                break;
            case CONFIRMACAO_3:
                statusMensagem = ESPERANDO_CONFIRMACAO;
                break;
            case PEDIR_CONEXAO:
                statusMensagem = ESPERANDO_CONFIRMACAO;
                break;
            default:
                statusMensagem = 0;
                break;
        }

        return statusMensagem;
    }

    public static void tratarPacote(DatagramPacket pacote, int status) throws IOException {

        switch (status) {
            case ESPERANDO_JOGADORES:
                tratarPacoteEsperandoJogadores(pacote);
                break;
            case ESPERANDO_INICIO:
                tratarPacoteEsperandoInicio(pacote);
                break;
            case ESPERANDO_DISTRIBUICAO:
                tratarPacoteEsperandoDistribuicao(pacote);
                break;
            case ESPERANDO_JOGADA:
                tratarPacoteEsperandoJogada(pacote);
                break;
            case ESPERANDO_CONFIRMACAO:
                tratarPacoteEsperandoConfirmacao(pacote);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Erro ao tratar pacote");
                break;
        }

    }

    private static void tratarPacoteEsperandoJogadores(DatagramPacket pacoteEsperandoJogadores) {

    }

    private static void tratarPacoteEsperandoInicio(DatagramPacket pacoteEsperandoInicio) {

    }

    private static void tratarPacoteEsperandoDistribuicao(DatagramPacket pacoteEsperandoDistribuicao) {

    }

    private static void tratarPacoteEsperandoJogada(DatagramPacket pacoteEsperandoJogada) {

    }

    private static void tratarPacoteEsperandoConfirmacao(DatagramPacket pacoteEsperandoConfirmacao) throws IOException {

        String mensagemPacote = new String(pacoteEsperandoConfirmacao.getData(), 0, pacoteEsperandoConfirmacao.getLength());

        switch (mensagemPacote) {
            case PEDIR_CONEXAO:
                Servidor.executa(pacoteEsperandoConfirmacao.getAddress().getHostAddress());
            case CONFIRMACAO_1:
                Ingressar.tpCampoStatus.setText("Conectado\nAguardando outros jogadores se conectarem...");
                break;
            case CONFIRMACAO_2:
                JOptionPane.showMessageDialog(null, "Você já está conectado");
                break;
            case CONFIRMACAO_3:
                JOptionPane.showMessageDialog(null, "O número de jogadores neste servidor já está completo");
                break;
            default:
                JOptionPane.showMessageDialog(null, "Algo de errado não está certo!");
                break;
        }

    }

}
