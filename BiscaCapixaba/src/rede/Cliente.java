package rede;

import biscacapixaba.MesaDeJogo;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Cliente extends Comunicador {

    public static String host;
    private int porta;
    protected static String nome;

    public Cliente(String host, int porta, String nome) {
        this.host = host;
        this.porta = porta;
        this.nome = MesaDeJogo.nomeJogador;

    }

    @Override
    void inicializarSocket() throws SocketException {
        socket = new DatagramSocket();
    }
}
