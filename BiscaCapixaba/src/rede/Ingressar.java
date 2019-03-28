/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rede;

import biscacapixaba.MesaDeJogo;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ingressar extends javax.swing.JFrame {

    public static String IpGerenciador;
    private Cliente cliente;
   

    /**
     * Creates new form Ingressar
     */
    public Ingressar() {
        initComponents();

    }

    private void abrirConexao() throws UnknownHostException, IOException {
        tpCampoStatus.setText("Conectando...");
        IpGerenciador = tpIpGerenciador.getText();        

        cliente = new Cliente(IpGerenciador, 5000, MesaDeJogo.nomeJogador);
        
        cliente.enviarPacote(Comunicador.PEDIR_CONEXAO, IpGerenciador, Servidor.portaDatagram);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                cliente.receberPacote();
            }
        });
        t.start();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pGerenciador = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tpIpGerenciador = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        bConectar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tpCampoStatus = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pGerenciador.setBorder(javax.swing.BorderFactory.createTitledBorder("Gerenciador"));

        jScrollPane2.setViewportView(tpIpGerenciador);

        jLabel1.setText("Ip do Gerenciador: ");

        javax.swing.GroupLayout pGerenciadorLayout = new javax.swing.GroupLayout(pGerenciador);
        pGerenciador.setLayout(pGerenciadorLayout);
        pGerenciadorLayout.setHorizontalGroup(
            pGerenciadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pGerenciadorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        pGerenciadorLayout.setVerticalGroup(
            pGerenciadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pGerenciadorLayout.createSequentialGroup()
                .addGroup(pGerenciadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        bConectar.setText("Conectar");
        bConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConectarActionPerformed(evt);
            }
        });

        tpCampoStatus.setEditable(false);
        jScrollPane1.setViewportView(tpCampoStatus);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(bConectar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 210, Short.MAX_VALUE))
                    .addComponent(pGerenciador, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pGerenciador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bConectar)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bConectarActionPerformed
        try {
            abrirConexao();
        } catch (IOException ex) {
            Logger.getLogger(Ingressar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_bConectarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ingressar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Ingressar().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bConectar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pGerenciador;
    protected static javax.swing.JTextPane tpCampoStatus;
    private javax.swing.JTextPane tpIpGerenciador;
    // End of variables declaration//GEN-END:variables
}