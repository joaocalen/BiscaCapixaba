/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rede;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Servidor extends Comunicador {

    public static int portaDatagram = 5000;

    private static ArrayList<Cliente> clientes;

    public Servidor() throws SocketException {
        this.clientes = new ArrayList<>();
    }

    @Override
    void inicializarSocket() throws SocketException {
        socket = new DatagramSocket(portaDatagram);
    }

    public static void executa(String ipCliente) throws IOException {

        // aceita um cliente
        try {
            boolean clienteAceito = true;

            switch (clientes.size()) {
                case 0:
                    if (ipCliente == null
                            ? IniciarPartidaEmRede.IpGerenciador == null
                            : ipCliente.equals(IniciarPartidaEmRede.IpGerenciador)) {
                        enviarPacote(CONFIRMACAO_2, ipCliente, portaDatagram);
                    } else {
                        Cliente c = new Cliente(ipCliente, portaDatagram, Cliente.nome);
                        clientes.add(c);
                        IniciarPartidaEmRede.tpJogador2.setText(ipCliente);
                        // confirmar e pedir nome
                        enviarPacote(CONFIRMACAO_1, ipCliente, portaDatagram);
                    }
                    break;
                case 1:
                    for (int j = 0; j < 1; j++) {
                        if ((ipCliente == null
                                ? clientes.get(j).host == null : ipCliente.equals(clientes.get(j).host))
                                || (ipCliente.equals(IniciarPartidaEmRede.IpGerenciador))) {
                            clienteAceito = false;
                            enviarPacote(CONFIRMACAO_2, ipCliente, portaDatagram);
                        }
                    }
                    if (clienteAceito) {
                        Cliente c = new Cliente(ipCliente, portaDatagram, Cliente.nome);
                        clientes.add(c);
                        IniciarPartidaEmRede.tpJogador3.setText(ipCliente);
                        // confirmar e pedir nome
                        enviarPacote(CONFIRMACAO_1, ipCliente, portaDatagram);
                    }
                    break;
                case 2:
                    for (int j = 0; j < 2; j++) {
                        if ((ipCliente == null
                                ? clientes.get(j).host == null : ipCliente.equals(clientes.get(j).host))
                                || (ipCliente == null ? IniciarPartidaEmRede.IpGerenciador == null
                                        : ipCliente.equals(IniciarPartidaEmRede.IpGerenciador))) {
                            clienteAceito = false;
                            enviarPacote(CONFIRMACAO_2, ipCliente, portaDatagram);
                        }
                    }
                    if (clienteAceito) {
                        Cliente c = new Cliente(ipCliente, portaDatagram, Cliente.nome);
                        clientes.add(c);
                        IniciarPartidaEmRede.tpJogador4.setText(ipCliente);
                        // confirmar e pedir nome
                        enviarPacote(CONFIRMACAO_1, ipCliente, portaDatagram);
                    }
                    break;
                default:
                    enviarPacote(CONFIRMACAO_3, ipCliente, portaDatagram);
                    break;
            }

        } catch (IOException io) {
            JOptionPane.showMessageDialog(null, "DEU RUIM!");
        }
    }

    public void distribuiMensagem(String msg) {
        for (Cliente cliente : this.clientes) {
        }
    }

}
