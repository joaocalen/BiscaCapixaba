/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biscacapixaba;

import rede.Ingressar;
import rede.IniciarPartidaEmRede;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class Carta {

    public static final int COPAS = 1;
    public static final int OUROS = 2;
    public static final int ESPADAS = 3;
    public static final int PAUS = 4;
    public static final int AS = 1;
    public static final int DAMA = 11;
    public static final int VALETE = 12;
    public static final int REI = 13;
    private int naipe;
    private int figura;

    public Carta(int naipe, int figura) {
        this.naipe = naipe;
        this.figura = figura;
    }

    public int getNaipe() {
        return naipe;
    }

    public int getFigura() {
        return figura;
    }

    /**
     * Compara duas cartas, <b>desprezando o naipe</b>.
     *
     * @param c1 uma carta a ser comparada
     * @param c2 outra carta a ser comparada
     * @return <ul>
     * <li>-1, se c1 &lt; c2,</li>
     * <li>0, se c1 = c2, ou</li>
     * <li>1, se c1 &gt; c2</li>
     * </ul>
     */
    public static int compara(Carta c1, Carta c2) {
        int resultado = 2;

        if (c1.getFigura() == c2.getFigura()) {
            resultado = 0;
        } else if (c1.getFigura() > c2.getFigura()) {
            resultado = 1;
        } else {
            resultado = -1;
        }

        return resultado;
    }

    public static String naipeToString(int naipe) {
        switch (naipe) {
            case OUROS:
                return "OUROS";
            case PAUS:
                return "PAUS";
            case COPAS:
                return "COPAS";
            case ESPADAS:
                return "ESPADAS";
        }
        return "";
    }

    public static String figuraToString(int figura) {
        switch (figura) {
            case AS:
                return "ÁS";
            case REI:
                return "REI";
            case DAMA:
                return "DAMA";
            case VALETE:
                return "VALETE";
            default:
                return Integer.toString(figura);
        }
    }
}

public class MesaDeJogo extends javax.swing.JFrame {

    private static final int JOGADOR_1 = 0;
    private static final int JOGADOR_2 = 1;
    private static final int JOGADOR_3 = 2;
    private static final int JOGADOR_4 = 3;
    private static final int FORA_DE_JOGO = 0;
    public static final int ESPERANDO_INICIO = 0;
    private static final int JOGO_EXPERIMENTAL = 1;
    private static final int JOGO_EM_REDE = 2;
    private static final Color BACKGROUND_COLOR = new Color(102, 204, 0);
//    private static final Color BACKGROUND_COLOR = new Color(108, 137, 80);
//    private static final Color BACKGROUND_COLOR = new Color(52, 143, 56);
    private static final Color COR_PADRAO = new Color(0, 0, 0);
    private static final LineBorder BORDA_OCULTA = new LineBorder(BACKGROUND_COLOR, 2);
    private static final LineBorder BORDA_DESTACADA2 = new LineBorder(Color.RED, 2);
    private static final LineBorder BORDA_DESTACADA = new LineBorder(Color.BLUE, 2);
    private static final Color DESTAQUE_NOME_JOGADOR = new Color(255, 255, 0);
    private static final String[] NOMES_JOGADORES_ALEATORIOS = {
        "Felipe", "Matheus", "Eugênio", "André"
    };
    public static String nomeJogador;
    // baralho: uma lista com as cartas do baralho
    private ArrayList<Carta> baralho;
    // maoJogador: um vetor de listas, sendo que cada elemento do vetor 
    //  representa o conjunto de cartas que está "nas mãos" de um jogador
    private ArrayList<Carta>[] maoJogador;
    // mesaJogador: um vetor de cartas que representa as cartas jogadas em
    //  uma rodada da partida
    private Carta[] mesaJogador;
    // monteJogador: um vetor de listas, sendo que cada elemento do vetor
    //  representa o conjunto de cartas que está "no monte" de cartas recolhidas
    //  por uma dupla do jogo durante uma partida
    private ArrayList<Carta>[] monteJogador;
    // tipoJogo: identifica o status do jogo no momento. Deve ser FORA_DE_JOGO,
    //  JOGO_EXPERIMENTAL ou JOGO_EM_REDE
    private int tipoJogo;
    // distribuidorAtual: indica o índice do jogador que faria a distribuição
    //  das cartas no jogo físico. Serve apenas para marcação do início da 
    //  distribuição das cartas na preparação da partida.
    private int distribuidorAtual;
    // jogadorAtual: indica o índice do jogador que fará a próxima jogada
    private int jogadorAtual;
    // primeiroJogadorRodada: indica o índice do jogador que iniciou a rodada
    private int primeiroJogadorRodada;
    // trunfo: indica o valor do trunfo da partida
    private int trunfo;
    // cartaTrunfo: é a carta que fica virada com a face para cima no meio da
    //  mesa de jogo, indicando qual é o trunfo da partida
    private Carta cartaTrunfo;
    // pontosDupla13: contém a quantidade de pontos acumulado no jogo pela
    //  dupla formada pelos jogadores 1 e 3
    private int pontosDupla13;
    // pontosDupla24: contém a quantidade de pontos acumulado no jogo pela
    //  dupla formada pelos jogadores 2 e 4
    private int pontosDupla24;
    // vetores com os elementos da interface gráfica que compõem a mesa de jogo
    private JLabel[][] labelMaoJogador;
    private JLabel[] labelMesaJogador;
    private JLabel[] labelMonteJogador;
    private JLabel[] labelNomeJogador;
    // gerador de números aleatórios usado durante o jogo
    private Random rand;
    // aguardandoSelecao: controle para indicar se o clique sobre uma carta do
    //  jogador 1 resultará em uma seleção de carta para jogar ou não
    private final boolean aguardandoSelecao = true;
    // indCartaPreSelecionada: indica a carta que foi pré-selecionada para
    //  ser jogada. A pré-seleção é o primeiro clique na carta, selecionando-a
    //  para jogar. É necessário clicar novamente na carta para confirmar a
    //  jogada
    private int indCartaPreSelecionada = - 1;
    /**
     * Se o primeiro jogador bater, será true
     */
    //private boolean bater = false;
    private boolean primeiraRodada = true;
    private int numeroRodadas = 1;
    private boolean seteLancada;
    private boolean setePrimeiraRodada = false;
    private boolean heley = false;
    private boolean heleyParceiros = false;
    private int rodadaSeteSaiu;
    private boolean seteSelecionada = false;
    private boolean asSelecionada = false;
    private int nivel = 0;
    private final int FACIL = 1;
    private final int MEDIO = 2;
    private final int DIFICIL = 3;
    private int jogadorGanhando;

    /**
     * Creates new form MesaDeJogo
     */
    public MesaDeJogo() {
        rand = new Random(System.currentTimeMillis());
        initComponents();
        this.getContentPane().setBackground(BACKGROUND_COLOR);
        iniciarVetoresLabels();

        configuracaoInicial();
        //lNomeJogador1.setText("João Felipe");

        solicitarNomeJogador();
    }

    private void solicitarNomeJogador() {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                identificarJogador();
            }
        };
        Timer t = new Timer();
        t.schedule(tt, 500);
    }

    /**
     * Reposiciona aleatoriamente as cartas do baralho.
     */
    private void embaralhar() {
        Collections.shuffle(baralho, rand);
    }

    /**
     * Prepara a interface gráfica para o início de uma partida, define a carta
     * trunfo como nula, define o tipo de jogo como fora de jogo e inicia o
     * baralho.
     */
    private void configuracaoInicial() {
        lCartaTrunfo.setVisible(false);
        // ocultar jogador 1
        lMaoJogador1Carta3.setVisible(false);
        lMaoJogador1Carta2.setVisible(false);
        lMaoJogador1Carta1.setVisible(false);
        lMesaJogador1.setVisible(false);
        lMonteJogador1.setVisible(false);
        // ocultar jogador 2
        lNomeJogador2.setText("");
        lMaoJogador2Carta1.setVisible(false);
        lMaoJogador2Carta2.setVisible(false);
        lMaoJogador2Carta3.setVisible(false);
        lMesaJogador2.setVisible(false);
        lMonteJogador2.setVisible(false);
        // ocultar jogador 3
        lNomeJogador3.setText("");
        lMaoJogador3Carta1.setVisible(false);
        lMaoJogador3Carta2.setVisible(false);
        lMaoJogador3Carta3.setVisible(false);
        lMesaJogador3.setVisible(false);
        // ocultar jogador 4
        lNomeJogador4.setText("");
        lMaoJogador4Carta1.setVisible(false);
        lMaoJogador4Carta2.setVisible(false);
        lMaoJogador4Carta3.setVisible(false);
        lMesaJogador4.setVisible(false);

        // ocultar status da partida e placar
        pStatusPartida.setVisible(true);
        pPlacar.setVisible(true);

        pontosDupla13 = 0;
        pontosDupla24 = 0;

        lBaralho.setVisible(true);

        lNomeDistribuidor.setVisible(false);
        lNaipeTrunfo.setVisible(false);
        lNomePrimeiroJogadorRodada.setVisible(false);

        cartaTrunfo = null;
        tipoJogo = FORA_DE_JOGO;
        iniciarBaralho();
        numeroRodadas = 1;

        primeiraRodada = true;
        numeroRodadas = 1;
        seteLancada = false;
        setePrimeiraRodada = false;
        heley = false;
        heleyParceiros = false;
        rodadaSeteSaiu = 0;
        seteSelecionada = false;
        asSelecionada = false;
    }

    /**
     * Apresenta os dados da partida atual no painel de status.
     */
    private void atualizarExibicaoStatusPartida() {
        lNomeDistribuidor.setText(labelNomeJogador[distribuidorAtual].getText());
        lNaipeTrunfo.setText(Carta.naipeToString(trunfo));
        lNomePrimeiroJogadorRodada.setText(labelNomeJogador[primeiroJogadorRodada].getText());
        pStatusPartida.setVisible(true);
    }

    /**
     * Apresenta o placar no painel apropriado.
     */
    private void atualizarExibicaoPlacar() {
        lNomeJogador1Placar.setText(lNomeJogador1.getText());
        lNomeJogador2Placar.setText(lNomeJogador2.getText());
        lNomeJogador3Placar.setText(lNomeJogador3.getText());
        lNomeJogador4Placar.setText(lNomeJogador4.getText());

        lPlacarDupla13.setText(Integer.toString(pontosDupla13));
        lPlacarDupla24.setText(Integer.toString(pontosDupla24));

        pPlacar.setVisible(true);
    }

    /**
     * Este método apenas faz a sobrecarga para receber um parâmetro da classe
     * Carta.
     *
     * @param objeto o objeto JLabel no qual a carta será exibida
     * @param carta a carta a ser exibida
     */
    private void definirCartaExibida(JLabel objeto, Carta carta) {
        definirCartaExibida(objeto, carta.getNaipe(), carta.getFigura());
    }

    /**
     * Exibe uma carta específica em um objeto JLabel da interface gráfica da
     * mesa de jogo.
     *
     * @param objeto o objeto JLabel no qual a carta será exibida
     * @param naipe o naipe da carta a exibir
     * @param figura a figura (ou número) da carta a exibir
     */
    private void definirCartaExibida(JLabel objeto, int naipe, int carta) {
        char codigoNaipe = ' ';
        switch (naipe) {
            case Carta.OUROS:
                codigoNaipe = 'd';
                break;
            case Carta.PAUS:
                codigoNaipe = 'c';
                break;
            case Carta.COPAS:
                codigoNaipe = 'h';
                break;
            case Carta.ESPADAS:
                codigoNaipe = 's';
                break;
        }
        char codigoCarta = ' ';
        if (carta >= 2 && carta <= 7) {
            codigoCarta = Integer.toString(carta).charAt(0);
        } else {
            switch (carta) {
                case Carta.AS:
                    codigoCarta = '1';
                    break;
                case Carta.VALETE:
                    codigoCarta = 'j';
                    break;
                case Carta.DAMA:
                    codigoCarta = 'q';
                    break;
                case Carta.REI:
                    codigoCarta = 'k';
                    break;
            }
        }
        objeto.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/imagens/baralho/" + codigoNaipe + codigoCarta
                        + ".png")));
        objeto.setVisible(true);
    }

    /**
     * Obtém do usuário o nome do jogador, através de uma caixa de diálogo de
     * entrada de dados, e apresenta este nome no label do nome do jogador 1.
     */
    private void identificarJogador() {
        do {
            nomeJogador = JOptionPane.showInputDialog(null,
                    "Nome:",
                    "Identificação",
                    JOptionPane.QUESTION_MESSAGE);
        } while (nomeJogador.trim().length() == 0);
        lNomeJogador1.setText(nomeJogador);
    }

    private void iniciarJogoExperimental() {
        configuracaoInicial();
        Object selectedValue = "";
        Object[] possiveisValores = {"Fácil", "Médio", "Difícil"};
        selectedValue = JOptionPane.showInputDialog(null,
                "Escolha o nível de dificuldade:", "Dificuldade",
                JOptionPane.INFORMATION_MESSAGE, null,
                possiveisValores, possiveisValores[0]);

        if (selectedValue == "Fácil") {
            nivel = FACIL;
        } else if (selectedValue == "Médio") {
            nivel = MEDIO;
        } else {
            nivel = DIFICIL;
        }

        tipoJogo = JOGO_EXPERIMENTAL;
        lNomeDistribuidor.setVisible(true);
        lNaipeTrunfo.setVisible(true);
        lNomePrimeiroJogadorRodada.setVisible(true);
        definirNomesAdversariosAleatoriamente();
        escolherPrimeiroDistribuidor();
        realizarDistribuicao();
        atualizarExibicaoStatusPartida();
        atualizarExibicaoPlacar();

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                escolherJogada();
            }
        };
        Timer t = new Timer();
        t.schedule(tt, 1000);

    }

    private void realizarDistribuicao() {
        embaralhar();
        baterOuCortar(distribuidorAtual);
        distribuirCartas();
        instanciarCartasAdversarios();
        jogadorAtual = primeiroJogadorRodada = (distribuidorAtual + 1) % 4;
    }

    private void processarJogada(int indCarta) {
        Carta c = maoJogador[jogadorAtual].get(indCarta);
        maoJogador[jogadorAtual].remove(indCarta);
        mesaJogador[jogadorAtual] = c;
        atualizarExibicaoMesaJogo();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                concluirJogada();
            }
        };
        Timer t = new Timer();
        t.schedule(tt, 1000);
    }

    private void recolherCartasMesa() {
        // processar jogada
        //boolean haTrunfo = mesaJogador[primeiroJogadorRodada].getNaipe() == trunfo;
        //seteLancada = haTrunfo && mesaJogador[primeiroJogadorRodada].getFigura() == 7;
        //int indJogadorSete = seteLancada ? primeiroJogadorRodada : -1;

        if (numeroRodadas == 1) {
            verificarSetePrimeiraRodada();
        }

        int vencedor;

        int[] ordemJogadores = new int[4];

        for (int i = 0; i < 4; i++) {
            Carta c = mesaJogador[(primeiroJogadorRodada + i) % 4];

            if (c.getFigura() == 7 && c.getNaipe() == trunfo) {
                seteLancada = true;
                rodadaSeteSaiu = numeroRodadas;
            }

            // para debug ------------------------------------------------------------------
            System.out.printf("Jogador %d: carta %6s de %7s",
                    ((primeiroJogadorRodada + i) % 4) + 1,
                    Carta.figuraToString(c.getFigura()),
                    Carta.naipeToString(c.getNaipe()));

            // -----------------------------------------------------------------------------
            //haTrunfo = haTrunfo || c.getNaipe() == trunfo;
            ordemJogadores[i] = (primeiroJogadorRodada + i) % 4;

        }
        vencedor = verificarJogadorGanhando(4);
        System.out.printf(
                "---\n>>> Vencedor: %d\n---\n", vencedor + 1);
        System.out.printf(
                "Adicionado ao monte do jogador %d:\n ", vencedor + 1);
        int monteVencedor;

        if (seteLancada && rodadaSeteSaiu == numeroRodadas) {
            verificarHeley(ordemJogadores);
        }

        if (vencedor == 3 || vencedor
                == 2) {
            monteVencedor = vencedor - 2;

        } else {
            monteVencedor = vencedor;
        }

        int pontos = 0;

        for (int i = 0; i < 4; i++) {

            monteJogador[monteVencedor].add(mesaJogador[i]);

            switch (mesaJogador[i].getFigura()) {
                case 1:
                    pontos = 11;
                    break;
                case 7:
                    pontos = 10;
                    break;
                case 11:
                    pontos = 2;
                    break;
                case 12:
                    pontos = 3;
                    break;
                case 13:
                    pontos = 4;
                    break;
                default:
                    pontos = 0;
                    break;
            }

            if (monteVencedor == 0) {
                pontosDupla13 += pontos;
                exibirMonteJogador(vencedor);

            } else {
                pontosDupla24 += pontos;
                exibirMonteJogador(vencedor);

            }
            System.out.printf("[%s - %s]; ",
                    Carta.figuraToString(mesaJogador[i].getFigura()),
                    Carta.naipeToString(mesaJogador[i].getNaipe()));
        }

        destacarMonteJogador(vencedor);

        // limpar mesa
        for (int i = 0;
                i < 4; i++) {
            mesaJogador[i] = null;
        }
        numeroRodadas++;

        atualizarExibicaoMesaJogo();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            System.out.println("Erro catastrófico ao realizar uma pausa entre as jogadas.");
        }

        // determina o primeiro jogador da próxima rodada
        jogadorAtual = primeiroJogadorRodada = vencedor;

        if (numeroRodadas
                == 11) {

            if (JOptionPane.showConfirmDialog(null,
                    "O Jogo Terminou, deseja começar outro?", "O jogo Terminou",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                    == JOptionPane.YES_OPTION) {
                iniciarJogoExperimental();
            } else {
                if (JOptionPane.showConfirmDialog(null,
                        "Deseja sair?", "Terminado",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                        == JOptionPane.YES_OPTION) {
                    configuracaoInicial();
                }

            }

        } else {
            realizarCompra();

            escolherJogada();
        }

        // processa (possíveis) pontos da rodada
    }

    private void atualizarExibicaoMesaJogo() {
        exibirCartasJogador1();
        exibirCartasAdversarios();
        exibirCartasMesa();
        exibirBaralho();
        atualizarExibicaoPlacar();
        this.repaint();
    }

    private void iniciarVetoresLabels() {
        labelMaoJogador = new JLabel[4][3];
        labelMaoJogador[0][0] = lMaoJogador1Carta1;
        labelMaoJogador[0][1] = lMaoJogador1Carta2;
        labelMaoJogador[0][2] = lMaoJogador1Carta3;
        labelMaoJogador[1][0] = lMaoJogador2Carta1;
        labelMaoJogador[1][1] = lMaoJogador2Carta2;
        labelMaoJogador[1][2] = lMaoJogador2Carta3;
        labelMaoJogador[2][0] = lMaoJogador3Carta1;
        labelMaoJogador[2][1] = lMaoJogador3Carta2;
        labelMaoJogador[2][2] = lMaoJogador3Carta3;
        labelMaoJogador[3][0] = lMaoJogador4Carta1;
        labelMaoJogador[3][1] = lMaoJogador4Carta2;
        labelMaoJogador[3][2] = lMaoJogador4Carta3;

        labelMesaJogador = new JLabel[4];
        labelMesaJogador[0] = lMesaJogador1;
        labelMesaJogador[1] = lMesaJogador2;
        labelMesaJogador[2] = lMesaJogador3;
        labelMesaJogador[3] = lMesaJogador4;

        labelMonteJogador = new JLabel[4];
        labelMonteJogador[0] = lMonteJogador1;
        labelMonteJogador[1] = lMonteJogador2;
        labelMonteJogador[2] = lMonteJogador1;
        labelMonteJogador[3] = lMonteJogador2;

        labelNomeJogador = new JLabel[4];
        labelNomeJogador[0] = lNomeJogador1;
        labelNomeJogador[1] = lNomeJogador2;
        labelNomeJogador[2] = lNomeJogador3;
        labelNomeJogador[3] = lNomeJogador4;
    }

    private void selecionarCarta(int indCarta) {
        if (aguardandoSelecao) {
            Carta c = maoJogador[0].get(indCarta);
            if (indCarta != indCartaPreSelecionada) {
                if (seteSelecionada && (c.getFigura() == 7 && c.getNaipe() == trunfo)) {
                    JOptionPane.showMessageDialog(null, "Calma ae Jovi, a sete não pode sair de canto!");
                    seteSelecionada = false;
                } else if (asSelecionada && (c.getFigura() == 1 && c.getNaipe() == trunfo)) {
                    JOptionPane.showMessageDialog(null, "Calma ae Jovi, a sete ainda não foi lançada, não se precipite!");
                    asSelecionada = false;
                }
                indCartaPreSelecionada = indCarta;
                atualizarDestaqueCarta();
            } else {
                processarJogada(indCarta);
                indCartaPreSelecionada = -1;
                atualizarDestaqueCarta();
            }
        }
    }

    private void verificarHeley(int[] ordemJogadores) {
        int indJogadorSete = - 1;
        boolean aux = false;
        for (int i = 0; i < 4; i++) {
            if (mesaJogador[ordemJogadores[i]].getNaipe() == trunfo && mesaJogador[ordemJogadores[i]].getFigura() == 1 && aux) {
                if ((indJogadorSete + 2) % 4 == (primeiroJogadorRodada + i) % 4) {
                    heleyParceiros = true;
                } else {
                    heley = true;
                }
            }
            if (mesaJogador[ordemJogadores[i]].getNaipe() == trunfo && mesaJogador[ordemJogadores[i]].getFigura() == 7) {
                indJogadorSete = ordemJogadores[i];
                aux = true;
            }
        }
        if (heley) {
            JOptionPane.showMessageDialog(null, "Heley!");
        } else if (heleyParceiros) {
            JOptionPane.showMessageDialog(null, "Heley de Parceiros! Tsc Tsc...");
        }

    }

    /**
     * Inicia a lista de cartas (baralho) e os vetores maoJogador, mesaJogador e
     * monteJogador. Isto consiste em instanciar esses objetos e atribuir os
     * elementos iniciais adequadamente. A saber, as cartas do jogo são
     * inseridas no baralho e cada elemento dos vetores indicados deve estar
     * vazio.
     */
    private void iniciarBaralho() {
        baralho = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            for (int j = 1; j < 8; j++) {

                Carta c = new Carta(i, j);

                baralho.add(c);

            }
        }

        for (int i = 1; i < 5; i++) {
            for (int j = 11; j < 14; j++) {

                Carta c = new Carta(i, j);

                baralho.add(c);

            }
        }

        maoJogador = new ArrayList[4];
        for (int i = 0; i < 4; i++) {
            maoJogador[i] = new ArrayList<>();
        }

        mesaJogador = new Carta[4];

        monteJogador = new ArrayList[2];

        for (int i = 0; i < 2; i++) {
            monteJogador[i] = new ArrayList<>();
        }

    }

    /**
     * Destaca o nome do jogador atual, através de chamada ao método
     * "destacarNomeJogador", e, se o jogador atual for o usuário, prepara a
     * interface para receber a escolha da carta a ser jogada ou, se o jogador
     * atual não for o usuário, faz a escolha aleatória da jogada através de
     * chamada ao método "escolherJogadaAdversarioExperimental".
     */
    private void escolherJogada() {

        destacarNomeJogador(jogadorAtual);

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if (jogadorAtual == 0) {
                } else {

                    escolherJogadaAdversarioExperimental();
                }
            }
        };
        Timer t = new Timer();
        t.schedule(tt, 500);

    }

    /**
     * Faz a escolha aleatória de uma das cartas que estão na mão do jogador
     * atual e faz o processamento da jogada através de chamada ao método
     * "processarJogada".
     */
    private void escolherJogadaAdversarioExperimental() {

        boolean seteNaMao = false;
        boolean asNaMao = false;
        boolean seteAsNaMao = false;

        Carta c;

        int cartasNaMesa = 0;
        for (int i = 0; i < 4; i++) {
            if (mesaJogador[i] != null) {
                cartasNaMesa++;
            }
        }

        switch (maoJogador[jogadorAtual].size()) {
            case 2:
                for (int i = 0; i < 2; i++) {
                    if (maoJogador[jogadorAtual].get(i).getFigura() == 7 && maoJogador[jogadorAtual].get(i).getNaipe() == trunfo) {
                        seteNaMao = true;
                    } else if (maoJogador[jogadorAtual].get(i).getFigura() == 1 && maoJogador[jogadorAtual].get(i).getNaipe() == trunfo) {
                        asNaMao = true;
                    }
                }
                break;
            case 3:
                for (int i = 0; i < 3; i++) {
                    if (maoJogador[jogadorAtual].get(i).getFigura() == 7 && maoJogador[jogadorAtual].get(i).getNaipe() == trunfo) {
                        seteNaMao = true;
                    } else if (maoJogador[jogadorAtual].get(i).getFigura() == 1 && maoJogador[jogadorAtual].get(i).getNaipe() == trunfo) {
                        asNaMao = true;
                    }
                }
                break;
            default:
                break;

        }

        if (seteNaMao && asNaMao) {
            seteAsNaMao = true;
        }

        switch (maoJogador[jogadorAtual].size()) {
            case 0:
                break;
            case 1:
                processarJogada(0);
                break;
            case 2:
                escolherJogadaComInteligencia(2, cartasNaMesa, seteAsNaMao);
                break;
            case 3:
                escolherJogadaComInteligencia(3, cartasNaMesa, seteAsNaMao);
                break;
        }
    }

    private void escolherJogadaComInteligencia(int cartasNaMao, int cartasNaMesa, boolean seteAsNaMao) {
        int jogada;
        if (nivel == MEDIO || nivel == DIFICIL) {
            jogada = escolherJogadaComInteligenciaHeley(cartasNaMao);
            if (jogada <= 2) {
                processarJogada(jogada);
            } else {
                int qtdNaMesa = 0;
                for (int i = 0; i < 4; i++) {
                    if (mesaJogador[i] != null) {
                        qtdNaMesa++;
                    }
                }
                jogadorGanhando = verificarJogadorGanhando(qtdNaMesa);
                if (cartasNaMesa == 0) {
                    jogada = escolherJogadaComInteligenciaPrimeiroJogadorRodada(cartasNaMao);
                } else {
                    jogada = escolherJogadaComInteligenciaEncarte(cartasNaMao, cartasNaMesa);
                }
                if (jogada <= 2) {
                    processarJogada(jogada);
                } else {

                }
            }
        } else {
            jogada = escolherJogadaAleatoriamente(cartasNaMao, cartasNaMesa, seteAsNaMao);
            processarJogada(jogada);
        }
    }

    private int escolherJogadaAleatoriamente(int cartasNaMao, int cartasNaMesa, boolean seteAsNaMao) {

        int cartaAleatoria = rand.nextInt(cartasNaMao);
        Carta c;
        c = maoJogador[jogadorAtual].get(cartaAleatoria);

        while ((c.getFigura() == 1 && c.getNaipe() == trunfo && seteLancada != true)
                || (c.getFigura() == 7 && c.getNaipe() == trunfo && cartasNaMesa == 3 && seteAsNaMao == false)) {

            if (seteAsNaMao && c.getFigura() == 7 && c.getNaipe() == trunfo) {
                break;
            }

            cartaAleatoria = rand.nextInt(cartasNaMao);
            c = maoJogador[jogadorAtual].get(cartaAleatoria);
        }

        return cartaAleatoria;

    }

    private int escolherJogadaComInteligenciaPrimeiroJogadorRodada(int cartasNaMao) {
        int jogada = 0;
        int maiorCarta, menorCarta;
        maiorCarta = verificarMaiorCartaDaMao(cartasNaMao);

        return jogada;
    }

    private int escolherJogadaComInteligenciaEncarte(int cartasNaMao, int cartasNaMesa) {
        int jogada = 0;

        switch (cartasNaMesa) {
            case 0:
                jogada = 4;
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                jogada = 4;
                break;
        }

        return jogada;
    }

    private int escolherJogadaComInteligenciaHeley(int cartasNaMao) {

        String[] cartaJogador = new String[cartasNaMao];
        boolean seteNaRodada = false;
        int jogada = 4;
        Carta c;

        for (int i = 0; i < cartasNaMao; i++) {
            c = maoJogador[jogadorAtual].get(i);
            cartaJogador[i] = Integer.toString(c.getFigura()) + Integer.toString(c.getNaipe());
        }

        int qtdNaMesa = 0;

        for (int i = 0; i < 4; i++) {

            if (mesaJogador[i] != null) {
                qtdNaMesa++;
            }
        }

        for (int i = 0; i < qtdNaMesa; i++) {

            if (i == 0) {
                c = mesaJogador[primeiroJogadorRodada];

                if (c.getFigura() == 7 && c.getNaipe() == trunfo) {
                    seteNaRodada = true;
                }

            } else {
                c = mesaJogador[(primeiroJogadorRodada + i) % 4];
                if (c.getFigura() == 7 && c.getNaipe() == trunfo) {
                    seteNaRodada = true;
                }
            }

        }

        String cartaIdeal = "1" + Integer.toString(trunfo);

        for (int i = 0; i < cartasNaMao; i++) {
            if (cartaIdeal.equals(cartaJogador[i]) && seteNaRodada) {
                jogada = i;
            }
        }

        return jogada;

    }

    private int verificarMaiorCartaDaMao(int cartasNaMao) {
        int maiorCarta = 4;
        Carta c;
        //Carta.compara(c1, c2);
        for (int i = 0; i < cartasNaMao; i++) {

        }

        return maiorCarta;
    }

    /**
     * Faz a exibição do objeto JLabel lBaralho, se ainda houver cartas no
     * baralho, e a exibição da carta de trunfo, se houver, através de chamada
     * ao método "definirCartaExibida".
     */
    private void exibirBaralho() {

        if (baralho.size() >= 4) {

            lBaralho.setVisible(true);

        } else {

            lBaralho.setVisible(false);
            lCartaTrunfo.setVisible(false);

        }

    }

    /**
     * Escolhe aleatoriamente os nomes dos jogadores adversários entre os nomes
     * que estão no vetor "NOMES_JOGADORES_ALEATORIOS" e os exibe nos objetos
     * JLabel "lNomeJogador2", "lNomeJogador3" e "lNomeJogador4".
     */
    private void definirNomesAdversariosAleatoriamente() {

        for (int i = JOGADOR_2; i <= JOGADOR_4; i++) {

            int resultado = rand.nextInt(4);

            if (NOMES_JOGADORES_ALEATORIOS[resultado] == null) {
                i--;
            } else {

                labelNomeJogador[i].setText(NOMES_JOGADORES_ALEATORIOS[resultado]);

                NOMES_JOGADORES_ALEATORIOS[resultado] = null;
            }
        }

    }

    /**
     * Exibe o label que indica a existência de cartas recolhidas e acumuladas
     * por um determinado jogador.
     *
     * @param jogador o número do jogador cujo monte deve ser exibido
     */
    private void exibirMonteJogador(int jogador) {

        if (jogador == 0 || jogador == 2) {

            lMonteJogador1.setVisible(true);

        }

        if (jogador == 1 || jogador == 3) {

            lMonteJogador2.setVisible(true);

        }

    }

    /**
     * Altera a cor dos nomes dos jogadores, deixando o nome do jogador indicado
     * com a cor diferente dos demais.
     *
     * @param jogador indica o jogador cujo nome deve ter cor diferente dos
     * demais
     */
    private void destacarNomeJogador(int jogador) {

        switch (jogador) {

            case 0:
                lNomeJogador1.setForeground(DESTAQUE_NOME_JOGADOR);
                lNomeJogador2.setForeground(COR_PADRAO);
                lNomeJogador3.setForeground(COR_PADRAO);
                lNomeJogador4.setForeground(COR_PADRAO);
                break;
            case 1:
                lNomeJogador1.setForeground(COR_PADRAO);
                lNomeJogador2.setForeground(DESTAQUE_NOME_JOGADOR);
                lNomeJogador3.setForeground(COR_PADRAO);
                lNomeJogador4.setForeground(COR_PADRAO);
                break;
            case 2:
                lNomeJogador1.setForeground(COR_PADRAO);
                lNomeJogador2.setForeground(COR_PADRAO);
                lNomeJogador3.setForeground(DESTAQUE_NOME_JOGADOR);
                lNomeJogador4.setForeground(COR_PADRAO);
                break;
            case 3:
                lNomeJogador1.setForeground(COR_PADRAO);
                lNomeJogador2.setForeground(COR_PADRAO);
                lNomeJogador3.setForeground(COR_PADRAO);
                lNomeJogador4.setForeground(DESTAQUE_NOME_JOGADOR);
                break;

        }

    }

    /**
     * Exibe temporariamente uma borda no monte do jogador indicado, para
     * destacá-lo. Depois de 2 s a borda é ocultada.
     *
     * @param jogador indica o jogador cujo monte deve ser destacado
     */
    private void destacarMonteJogador(int jogador) {

        if (jogador == 0 || jogador == 2) {

            lMonteJogador1.setBorder(BORDA_DESTACADA);

            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    lMonteJogador1.setBorder(BORDA_OCULTA);
                }
            };
            Timer t = new Timer();
            t.schedule(tt, 2000);

        } else {
            lMonteJogador2.setBorder(BORDA_DESTACADA);

            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    lMonteJogador2.setBorder(BORDA_OCULTA);
                }
            };
            Timer t = new Timer();
            t.schedule(tt, 2000);

        }

    }

    /**
     * Define o distribuidor atual aleatoriamente entre os quatro jogadores.
     */
    private void escolherPrimeiroDistribuidor() {

        distribuidorAtual = rand.nextInt(4);

    }

    /**
     * Apresenta uma caixa de diálogo com as opções "Bater" e "Cortar", para que
     * o usuário escolha a opção desejada, faz a leitura da opção escolhida e,
     * se a opção for "cortar", faz a escolha aleatória da carta de trunfo
     * através de chamada ao método "selecionarTrunfo". Se o jogo for do tipo
     * experimental e o jogador a escolher não for o jogador 1, a escolha de
     * bater ou cortar deve ser aleatória, com 25% de chance de bater.
     */
    private void baterOuCortar(int jogadorBaterOuCortar) {

        Object selectedValue = "";

        if (jogadorBaterOuCortar == 1) {
            Object[] possiveisValores = {"BATER", "CORTAR"};
            selectedValue = JOptionPane.showInputDialog(null,
                    "Escolha:", "Bate ou Corta?",
                    JOptionPane.INFORMATION_MESSAGE, null,
                    possiveisValores, possiveisValores[0]);
        } else {
            int cortarBater = rand.nextInt(4);

            switch (cortarBater) {
                case 0:
                    selectedValue = "CORTAR";
                    break;
                case 1:
                    selectedValue = "CORTAR";
                    break;
                case 2:
                    selectedValue = "CORTAR";
                    break;
                case 3:
                    selectedValue = "BATER";
                    break;
            }

        }

        if (selectedValue == "CORTAR") {
            selecionarTrunfo();
        } else {
            trunfo = 1;
        }

    }

    /**
     * Escolhe aleatoriamente uma carta do baralho para ser o trunfo. A carta
     * selecionada deve ser exibida no label lCartaTrunfo. Deve ser seguida a
     * regra para escolha da carta de trunfo.
     */
    private void selecionarTrunfo() {

        int trunfoRandom = rand.nextInt(40);

        Carta c = baralho.get(trunfoRandom);

        if ((c.getFigura() == 1) || (c.getFigura() == 7)) {

            lCartaTrunfo.setVisible(false);

            switch (c.getNaipe()) {

                case 1:
                    trunfo = 2;
                    break;

                case 2:
                    trunfo = 1;
                    break;

                case 3:
                    trunfo = 4;
                    break;

                case 4:
                    trunfo = 3;
                    break;

            }

        } else {

            trunfo = c.getNaipe();

            Carta d = c;

            baralho.remove(trunfoRandom);

            baralho.add(d);

            definirCartaExibida(lCartaTrunfo, d);

        }
    }

    /**
     * Faz a distribuição de três cartas para cada jogador. As cartas ficam
     * armazenadas no vetor "maoJogador". Após a distribuição, deve ser feita a
     * exibição das cartas do jogador 1, de modo a permitir sua visualização,
     * através de chamada ao método "exibirCartasJogador1", e os versos das
     * cartas dos adversários, através de chamada ao método
     * "exibirCartasAdversarios".
     */
    private void distribuirCartas() {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {

                maoJogador[i].add(baralho.get(0));

                baralho.remove(0);
            }
        }

        exibirCartasJogador1();
        exibirCartasAdversarios();

    }

    /**
     * Finaliza uma jogada. Verifica se todos os jogadores já fizeram a sua
     * jogada. Se isto for verdadeiro, faz o recolhimento das cartas através de
     * chamada ao método "recolherCartasMesa" e faz a compra (se ainda houver
     * cartas a comprar). Se ainda houver jogadores que não fizeram as suas
     * jogadas, atualiza a variável "jogadorAtual" para o próximo jogador na
     * sequência, de acordo com a regra do jogo.<br/>
     * <br/>
     * Por fim, atualiza a exibição do estado da partida através de chamada ao
     * método "atualizarExibicaoStatusPartida" e, se a partida ainda não estiver
     * concluída, executa o método "escolherJogada".
     */
    private void concluirJogada() {

        int nulo = 0;

        for (int i = 0; i < 4; i++) {

            if (mesaJogador[i] != null) {
                nulo++;
            }

        }

        if (nulo == 4) {

            recolherCartasMesa();
            atualizarExibicaoStatusPartida();

        } else {
            switch (jogadorAtual) {
                case 0:
                    jogadorAtual = 1;
                    break;
                case 1:
                    jogadorAtual = 2;
                    break;
                case 2:
                    jogadorAtual = 3;
                    break;
                case 3:
                    jogadorAtual = 0;
                    break;
            }
            escolherJogada();
        }
    }

    /**
     * Verifica o jogador que está vencendo a rodada, recebe como parâmetro o
     * número de cartas dispostas na mesa.
     */
    private int verificarJogadorGanhando(int cartasNaMesa) {
        /*
         if (c.getNaipe() == trunfo
         && (mesaJogador[vencedor].getNaipe() != trunfo
         || Carta.compara(c, mesaJogador[vencedor]) == 1)) {
         vencedor = (primeiroJogadorRodada + i) % 4;
         if (c.getFigura() == 7) {
         seteLancada = true;
         indJogadorSete = (primeiroJogadorRodada + i) % 4;
         } else if (c.getFigura() == Carta.AS && seteLancada) {
         heley = true;
         if ((indJogadorSete + 2) % 4 == (primeiroJogadorRodada + i) % 4) {
         heleyParceiros = true;
         }
         }
         } else if (!haTrunfo) {
         if (c.getNaipe() == mesaJogador[vencedor].getNaipe()
         && Carta.compara(c, mesaJogador[vencedor]) == 1) {
         vencedor = (primeiroJogadorRodada + i) % 4;
         }
         }
         */
        int vencedor = primeiroJogadorRodada;
        if (cartasNaMesa == 0) {
        } else {
            Carta c;
            for (int i = 0; i < cartasNaMesa; i++) {
                c = mesaJogador[(primeiroJogadorRodada + i) % 4];
                if (c.getNaipe() == trunfo
                        && (mesaJogador[vencedor].getNaipe() != trunfo)) {

                    vencedor = (primeiroJogadorRodada + i) % 4;

                } else if (c.getNaipe() == trunfo
                        && (mesaJogador[vencedor].getNaipe() == trunfo)) {

                    if (c.getFigura() == 1) {
                        vencedor = (primeiroJogadorRodada + i) % 4;
                    } else if (c.getFigura() == 7 && mesaJogador[vencedor].getFigura() != 1) {
                        vencedor = (primeiroJogadorRodada + i) % 4;
                    } else if (c.getFigura() > mesaJogador[vencedor].getFigura()) {
                        if (mesaJogador[vencedor].getFigura() != 7 && mesaJogador[vencedor].getFigura() != 1) {
                            vencedor = (primeiroJogadorRodada + i) % 4;
                        }
                    }
                } else if ((c.getNaipe() != trunfo
                        && mesaJogador[vencedor].getNaipe() != trunfo)
                        && (c.getNaipe() == mesaJogador[vencedor].getNaipe())) {
                    if (c.getFigura() == 1) {
                        vencedor = (primeiroJogadorRodada + i) % 4;
                    } else if (c.getFigura() == 7 && mesaJogador[vencedor].getFigura() != 1) {
                        vencedor = (primeiroJogadorRodada + i) % 4;
                    } else if (c.getFigura() > mesaJogador[vencedor].getFigura()) {
                        if (mesaJogador[vencedor].getFigura() != 7 && mesaJogador[vencedor].getFigura() != 1) {
                            vencedor = (primeiroJogadorRodada + i) % 4;
                        }
                    }
                }
            }
        }
        return vencedor;
    }

    /**
     * Para cada jogador, faz a transferência de uma carta do início do baralho
     * para a lista de cartas correspondente à mão deste jogador. Esta lista
     * está no vetor "maoJogador". A compra deve ser iniciada do jogador que
     * venceu a última rodada. Ao fim da operação de compra, a mesa de jogo deve
     * ter sua exibição atualizada através de chamada ao método
     * "atualizarExibicaoMesaJogo".
     */
    private void realizarCompra() {

        int vencedor = jogadorAtual;

        int jogador1 = 0;
        int jogador2 = 0;
        int jogador3 = 0;
        int jogador4 = 0;

        switch (vencedor) {

            case 0:
                jogador1 = 0;
                jogador2 = 1;
                jogador3 = 2;
                jogador4 = 3;
                break;
            case 1:
                jogador1 = 1;
                jogador2 = 2;
                jogador3 = 3;
                jogador4 = 0;
                break;
            case 2:
                jogador1 = 2;
                jogador2 = 3;
                jogador3 = 0;
                jogador4 = 1;
                break;
            case 3:
                jogador1 = 3;
                jogador2 = 0;
                jogador3 = 1;
                jogador4 = 2;
                break;

        }

        if (baralho.size() >= 4) {

            Carta c;

            c = baralho.get(0);
            maoJogador[jogador1].add(c);
            if (jogador1 == 0) {
                definirCartaExibida(labelMaoJogador[0][2], c.getNaipe(), c.getFigura());
            }
            baralho.remove(0);

            c = baralho.get(0);
            if (jogador2 == 0) {
                definirCartaExibida(labelMaoJogador[0][2], c.getNaipe(), c.getFigura());
            }
            maoJogador[jogador2].add(c);
            baralho.remove(0);

            c = baralho.get(0);
            if (jogador3 == 0) {
                definirCartaExibida(labelMaoJogador[0][2], c.getNaipe(), c.getFigura());
            }
            maoJogador[jogador3].add(c);
            baralho.remove(0);

            c = baralho.get(0);
            if (jogador4 == 0) {
                definirCartaExibida(labelMaoJogador[0][2], c.getNaipe(), c.getFigura());
            }
            maoJogador[jogador4].add(c);
            baralho.remove(0);
        }
        atualizarExibicaoMesaJogo();

    }

    private void verificarSetePrimeiraRodada() {

        if (mesaJogador[primeiroJogadorRodada].getFigura() == 7
                && mesaJogador[primeiroJogadorRodada].getNaipe() == trunfo) {
            setePrimeiraRodada = true;
            JOptionPane.showInputDialog("1 ponto por começar de 7!");
        }

    }

    /**
     * Faz a exibição das cartas da mão do jogador 1, de modo que fiquem
     * visíveis para o usuário. Isso é feito com chamadas ao método
     * "definirCartaExibida" para cada carta da mão do jogador 1.
     */
    private void exibirCartasJogador1() {
        Carta c;
        switch (maoJogador[0].size()) {

            case 0:
                lMaoJogador1Carta1.setVisible(false);
                lMaoJogador1Carta2.setVisible(false);
                lMaoJogador1Carta3.setVisible(false);
                break;
            case 1:
                c = maoJogador[0].get(0);
                definirCartaExibida(lMaoJogador1Carta1, c.getNaipe(), c.getFigura());
                lMaoJogador1Carta2.setVisible(false);
                lMaoJogador1Carta3.setVisible(false);
                break;
            case 2:
                c = maoJogador[0].get(0);
                definirCartaExibida(lMaoJogador1Carta1, c.getNaipe(), c.getFigura());
                c = maoJogador[0].get(1);
                definirCartaExibida(lMaoJogador1Carta2, c.getNaipe(), c.getFigura());
                lMaoJogador1Carta3.setVisible(false);
                break;
            case 3:
                c = maoJogador[0].get(0);
                definirCartaExibida(lMaoJogador1Carta1, c.getNaipe(), c.getFigura());
                c = maoJogador[0].get(1);
                definirCartaExibida(lMaoJogador1Carta2, c.getNaipe(), c.getFigura());
                c = maoJogador[0].get(2);
                definirCartaExibida(lMaoJogador1Carta3, c.getNaipe(), c.getFigura());
                break;

        }

    }

    private void instanciarCartasAdversarios() {
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 3; j++) {

                if (i == 2) {

                    labelMaoJogador[i][j].setIcon(new javax.swing.ImageIcon(getClass()
                            .getResource("/imagens/baralho/b1fv.png")));

                }

                if (((i == 1) || (i == 3))) {
                    labelMaoJogador[i][j].setIcon(new javax.swing.ImageIcon(getClass()
                            .getResource("/imagens/baralho/b1fh.png")));

                }

            }
        }
    }

    /**
     * Faz a exibição do verso das cartas dos jogadores adversários, cuidando de
     * exibir apenas a quantidade de versos de cartas correspondente à
     * quantidade de cartas que o jogador possui em mãos.
     */
    private void exibirCartasAdversarios() {

        for (int i = 1; i < 4; i++) {

            if (i == 1) {

                switch (maoJogador[i].size()) {

                    case 0:
                        labelMaoJogador[i][0].setVisible(false);
                        labelMaoJogador[i][1].setVisible(false);
                        labelMaoJogador[i][2].setVisible(false);
                        break;
                    case 1:
                        labelMaoJogador[i][0].setVisible(true);
                        labelMaoJogador[i][1].setVisible(false);
                        labelMaoJogador[i][2].setVisible(false);
                        break;
                    case 2:
                        labelMaoJogador[i][0].setVisible(true);
                        labelMaoJogador[i][1].setVisible(true);
                        labelMaoJogador[i][2].setVisible(false);
                        break;
                    case 3:
                        labelMaoJogador[i][0].setVisible(true);
                        labelMaoJogador[i][1].setVisible(true);
                        labelMaoJogador[i][2].setVisible(true);
                        break;

                }
            }
            if (i == 2) {

                switch (maoJogador[i].size()) {

                    case 0:
                        labelMaoJogador[i][0].setVisible(false);
                        labelMaoJogador[i][1].setVisible(false);
                        labelMaoJogador[i][2].setVisible(false);
                        break;
                    case 1:
                        labelMaoJogador[i][0].setVisible(true);
                        labelMaoJogador[i][1].setVisible(false);
                        labelMaoJogador[i][2].setVisible(false);
                        break;
                    case 2:
                        labelMaoJogador[i][0].setVisible(true);
                        labelMaoJogador[i][1].setVisible(true);
                        labelMaoJogador[i][2].setVisible(false);
                        break;
                    case 3:
                        labelMaoJogador[i][0].setVisible(true);
                        labelMaoJogador[i][1].setVisible(true);
                        labelMaoJogador[i][2].setVisible(true);
                        break;

                }
            }
            if (i == 3) {

                switch (maoJogador[i].size()) {

                    case 0:
                        labelMaoJogador[i][0].setVisible(false);
                        labelMaoJogador[i][1].setVisible(false);
                        labelMaoJogador[i][2].setVisible(false);
                        break;
                    case 1:
                        labelMaoJogador[i][0].setVisible(true);
                        labelMaoJogador[i][1].setVisible(false);
                        labelMaoJogador[i][2].setVisible(false);
                        break;
                    case 2:
                        labelMaoJogador[i][0].setVisible(true);
                        labelMaoJogador[i][1].setVisible(true);
                        labelMaoJogador[i][2].setVisible(false);
                        break;
                    case 3:
                        labelMaoJogador[i][0].setVisible(true);
                        labelMaoJogador[i][1].setVisible(true);
                        labelMaoJogador[i][2].setVisible(true);
                        break;

                }
            }

        }

    }

    /**
     * Faz a exibição das cartas que já foram jogadas na rodada atual. Isto é
     * feito observando o vetor "mesaJogador" e usando o método
     * "definirCartaExibida".
     */
    private void exibirCartasMesa() {
        int qtd = 0;
        for (int i = 0; i < 4; i++) {

            if (mesaJogador[i] != null) {
                qtd++;
            }
        }
        Carta c;
        if (qtd == 0) {
            lMesaJogador1.setVisible(false);
            lMesaJogador2.setVisible(false);
            lMesaJogador3.setVisible(false);
            lMesaJogador4.setVisible(false);
        } else {

            switch (jogadorAtual) {
                case 0:
                    c = mesaJogador[0];
                    definirCartaExibida(lMesaJogador1, c.getNaipe(), c.getFigura());
                    break;
                case 1:
                    c = mesaJogador[1];
                    definirCartaExibida(lMesaJogador2, c.getNaipe(), c.getFigura());
                    break;
                case 2:
                    c = mesaJogador[2];
                    definirCartaExibida(lMesaJogador3, c.getNaipe(), c.getFigura());
                    break;
                case 3:
                    c = mesaJogador[3];
                    definirCartaExibida(lMesaJogador4, c.getNaipe(), c.getFigura());
                    break;

            }
        }
    }

    /**
     * Faz o destaque da carta pré-selecionada pelo usuário para ser jogada. A
     * pré-seleção está armazenada na variável "indCartaPreSelecionada" e os
     * objetos a serem destacados estão no vetor "labelMaoJogador". O destaque é
     * feito através da mudança da borda do objeto JLabel para BORDA_DESTACADA.
     * Quando não está destacada, a borda é BORDA_OCULTA.
     */
    private void atualizarDestaqueCarta() {

        switch (indCartaPreSelecionada) {
            case 0:
                lMaoJogador1Carta1.setBorder(BORDA_DESTACADA);
                lMaoJogador1Carta2.setBorder(BORDA_OCULTA);
                lMaoJogador1Carta3.setBorder(BORDA_OCULTA);
                break;
            case 1:
                lMaoJogador1Carta1.setBorder(BORDA_OCULTA);
                lMaoJogador1Carta2.setBorder(BORDA_DESTACADA);
                lMaoJogador1Carta3.setBorder(BORDA_OCULTA);
                break;
            case 2:
                lMaoJogador1Carta1.setBorder(BORDA_OCULTA);
                lMaoJogador1Carta2.setBorder(BORDA_OCULTA);
                lMaoJogador1Carta3.setBorder(BORDA_DESTACADA);
                break;
            default:
                lMaoJogador1Carta1.setBorder(BORDA_OCULTA);
                lMaoJogador1Carta2.setBorder(BORDA_OCULTA);
                lMaoJogador1Carta3.setBorder(BORDA_OCULTA);
                break;

        }

    }

    private void ingressarPartida() {
        Ingressar ingressar = new Ingressar();
        ingressar.setVisible(true);
    }

    private void iniciarPartidaEmRede() throws IOException {
        IniciarPartidaEmRede iniciar = new IniciarPartidaEmRede();
        iniciar.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lMaoJogador3Carta1 = new javax.swing.JLabel();
        lMaoJogador3Carta2 = new javax.swing.JLabel();
        lMaoJogador3Carta3 = new javax.swing.JLabel();
        lNomeJogador3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lBaralho = new javax.swing.JLabel();
        lCartaTrunfo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lNomeJogador4 = new javax.swing.JLabel();
        lMaoJogador4Carta1 = new javax.swing.JLabel();
        lMaoJogador4Carta3 = new javax.swing.JLabel();
        lMaoJogador4Carta2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lMaoJogador1Carta1 = new javax.swing.JLabel();
        lMaoJogador1Carta2 = new javax.swing.JLabel();
        lMaoJogador1Carta3 = new javax.swing.JLabel();
        lNomeJogador1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lNomeJogador2 = new javax.swing.JLabel();
        lMaoJogador2Carta1 = new javax.swing.JLabel();
        lMaoJogador2Carta2 = new javax.swing.JLabel();
        lMaoJogador2Carta3 = new javax.swing.JLabel();
        lMesaJogador1 = new javax.swing.JLabel();
        lMesaJogador3 = new javax.swing.JLabel();
        lMesaJogador4 = new javax.swing.JLabel();
        lMesaJogador2 = new javax.swing.JLabel();
        lMonteJogador1 = new javax.swing.JLabel();
        lMonteJogador2 = new javax.swing.JLabel();
        pStatusPartida = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lNomeDistribuidor = new javax.swing.JLabel();
        lNaipeTrunfo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lNomePrimeiroJogadorRodada = new javax.swing.JLabel();
        pPlacar = new javax.swing.JPanel();
        lNomeJogador1Placar = new javax.swing.JLabel();
        lNomeJogador3Placar = new javax.swing.JLabel();
        lPlacarDupla13 = new javax.swing.JLabel();
        lNomeJogador2Placar = new javax.swing.JLabel();
        lNomeJogador4Placar = new javax.swing.JLabel();
        lPlacarDupla24 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        miIniciarPartida = new javax.swing.JMenu();
        miIngressarEmPartida = new javax.swing.JMenuItem();
        miIngressarPartida = new javax.swing.JMenuItem();
        miIniciarPartidaEmRede = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        miSair = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bisca capixaba");
        setMinimumSize(new java.awt.Dimension(869, 721));
        setName("fMesaDeJogo"); // NOI18N
        setPreferredSize(new java.awt.Dimension(869, 721));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lMaoJogador3Carta1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fv.png"))); // NOI18N
        jPanel1.add(lMaoJogador3Carta1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 39, -1, -1));

        lMaoJogador3Carta2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fv.png"))); // NOI18N
        jPanel1.add(lMaoJogador3Carta2, new org.netbeans.lib.awtextra.AbsoluteConstraints(89, 39, -1, -1));

        lMaoJogador3Carta3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fv.png"))); // NOI18N
        jPanel1.add(lMaoJogador3Carta3, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 39, -1, -1));

        lNomeJogador3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lNomeJogador3.setText("jogador 3");
        lNomeJogador3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(lNomeJogador3, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 12, 230, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, -1, 150));

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lBaralho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fh.png"))); // NOI18N
        jPanel2.add(lBaralho, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        lCartaTrunfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/d3.png"))); // NOI18N
        jPanel2.add(lCartaTrunfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, -1, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 270, 135, 131));

        jPanel3.setOpaque(false);

        lNomeJogador4.setText("jogador 4");

        lMaoJogador4Carta1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fh.png"))); // NOI18N

        lMaoJogador4Carta3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fh.png"))); // NOI18N

        lMaoJogador4Carta2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fh.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lMaoJogador4Carta1)
                    .addComponent(lMaoJogador4Carta2)
                    .addComponent(lMaoJogador4Carta3))
                .addContainerGap(54, Short.MAX_VALUE))
            .addComponent(lNomeJogador4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(lNomeJogador4)
                .addGap(6, 6, 6)
                .addComponent(lMaoJogador4Carta1)
                .addGap(6, 6, 6)
                .addComponent(lMaoJogador4Carta2)
                .addGap(6, 6, 6)
                .addComponent(lMaoJogador4Carta3)
                .addGap(0, 15, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 170, 260));

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lMaoJogador1Carta1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/c1.png"))); // NOI18N
        lMaoJogador1Carta1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 204, 0), 2));
        lMaoJogador1Carta1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lMaoJogador1Carta1MouseClicked(evt);
            }
        });
        jPanel4.add(lMaoJogador1Carta1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lMaoJogador1Carta2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/dq.png"))); // NOI18N
        lMaoJogador1Carta2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 204, 0), 2));
        lMaoJogador1Carta2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lMaoJogador1Carta2MouseClicked(evt);
            }
        });
        jPanel4.add(lMaoJogador1Carta2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, -1, -1));

        lMaoJogador1Carta3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/s6.png"))); // NOI18N
        lMaoJogador1Carta3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 204, 0), 2));
        lMaoJogador1Carta3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lMaoJogador1Carta3MouseClicked(evt);
            }
        });
        jPanel4.add(lMaoJogador1Carta3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, -1, -1));

        lNomeJogador1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lNomeJogador1.setText("jogador 1");
        jPanel4.add(lNomeJogador1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 250, -1));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 530, 250, 140));

        jPanel5.setOpaque(false);

        lNomeJogador2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lNomeJogador2.setText("jogador 2");

        lMaoJogador2Carta1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fh.png"))); // NOI18N

        lMaoJogador2Carta2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fh.png"))); // NOI18N

        lMaoJogador2Carta3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fh.png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lMaoJogador2Carta1)
                    .addComponent(lMaoJogador2Carta2)
                    .addComponent(lMaoJogador2Carta3))
                .addContainerGap(24, Short.MAX_VALUE))
            .addComponent(lNomeJogador2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(lNomeJogador2)
                .addGap(6, 6, 6)
                .addComponent(lMaoJogador2Carta1)
                .addGap(6, 6, 6)
                .addComponent(lMaoJogador2Carta2)
                .addGap(6, 6, 6)
                .addComponent(lMaoJogador2Carta3)
                .addGap(0, 15, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 190, 170, 260));

        lMesaJogador1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/c6.png"))); // NOI18N
        lMesaJogador1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 204, 0), 2));
        getContentPane().add(lMesaJogador1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 420, -1, -1));

        lMesaJogador3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/hj.png"))); // NOI18N
        lMesaJogador3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 204, 0), 2));
        getContentPane().add(lMesaJogador3, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 150, -1, -1));

        lMesaJogador4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/sk.png"))); // NOI18N
        lMesaJogador4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 204, 0), 2));
        getContentPane().add(lMesaJogador4, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 280, -1, -1));

        lMesaJogador2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/d1.png"))); // NOI18N
        lMesaJogador2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 204, 0), 2));
        getContentPane().add(lMesaJogador2, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 280, -1, -1));

        lMonteJogador1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fh.png"))); // NOI18N
        lMonteJogador1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 204, 0), 2));
        getContentPane().add(lMonteJogador1, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 560, -1, -1));

        lMonteJogador2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/baralho/b1fv.png"))); // NOI18N
        lMonteJogador2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 204, 0), 2));
        getContentPane().add(lMonteJogador2, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 50, -1, -1));

        pStatusPartida.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 0)));
        pStatusPartida.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel1.setText("Distribuidor:");

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel2.setText("Trunfo:");

        lNomeDistribuidor.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lNomeDistribuidor.setText("...");

        lNaipeTrunfo.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lNaipeTrunfo.setText("...");

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel3.setText("Inicia rodada:");

        lNomePrimeiroJogadorRodada.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lNomePrimeiroJogadorRodada.setText("...");

        javax.swing.GroupLayout pStatusPartidaLayout = new javax.swing.GroupLayout(pStatusPartida);
        pStatusPartida.setLayout(pStatusPartidaLayout);
        pStatusPartidaLayout.setHorizontalGroup(
            pStatusPartidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pStatusPartidaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pStatusPartidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pStatusPartidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lNaipeTrunfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lNomeDistribuidor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lNomePrimeiroJogadorRodada, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                .addContainerGap())
        );
        pStatusPartidaLayout.setVerticalGroup(
            pStatusPartidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pStatusPartidaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pStatusPartidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lNomeDistribuidor, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(18, 18, 18)
                .addGroup(pStatusPartidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lNaipeTrunfo, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(18, 18, 18)
                .addGroup(pStatusPartidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lNomePrimeiroJogadorRodada, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(pStatusPartida, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 250, 100));

        pPlacar.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 0)), "PLACAR"));
        pPlacar.setOpaque(false);

        lNomeJogador1Placar.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lNomeJogador1Placar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lNomeJogador1Placar.setText("Jogador 1");

        lNomeJogador3Placar.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lNomeJogador3Placar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lNomeJogador3Placar.setText("Jogador 3");

        lPlacarDupla13.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lPlacarDupla13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lPlacarDupla13.setText("0");

        lNomeJogador2Placar.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lNomeJogador2Placar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lNomeJogador2Placar.setText("Jogador 2");

        lNomeJogador4Placar.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        lNomeJogador4Placar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lNomeJogador4Placar.setText("Jogador 4");

        lPlacarDupla24.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lPlacarDupla24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lPlacarDupla24.setText("0");

        javax.swing.GroupLayout pPlacarLayout = new javax.swing.GroupLayout(pPlacar);
        pPlacar.setLayout(pPlacarLayout);
        pPlacarLayout.setHorizontalGroup(
            pPlacarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPlacarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pPlacarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lNomeJogador1Placar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lNomeJogador3Placar, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                    .addComponent(lPlacarDupla13, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(lNomeJogador2Placar, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(lNomeJogador4Placar, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(lPlacarDupla24, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE))
                .addContainerGap())
        );
        pPlacarLayout.setVerticalGroup(
            pPlacarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPlacarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lNomeJogador1Placar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lNomeJogador3Placar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lPlacarDupla13)
                .addGap(18, 18, 18)
                .addComponent(lNomeJogador2Placar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lNomeJogador4Placar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lPlacarDupla24)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(pPlacar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 470, 250, 180));

        miIniciarPartida.setMnemonic('j');
        miIniciarPartida.setText("Jogo");

        miIngressarEmPartida.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        miIngressarEmPartida.setMnemonic('i');
        miIngressarEmPartida.setText("Iniciar partida");
        miIngressarEmPartida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miIngressarEmPartidaActionPerformed(evt);
            }
        });
        miIniciarPartida.add(miIngressarEmPartida);

        miIngressarPartida.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        miIngressarPartida.setMnemonic('n');
        miIngressarPartida.setText("Ingressar em partida em rede");
        miIngressarPartida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miIngressarPartidaActionPerformed(evt);
            }
        });
        miIniciarPartida.add(miIngressarPartida);

        miIniciarPartidaEmRede.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        miIniciarPartidaEmRede.setText("Iniciar partida em rede");
        miIniciarPartidaEmRede.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miIniciarPartidaEmRedeActionPerformed(evt);
            }
        });
        miIniciarPartida.add(miIniciarPartidaEmRede);
        miIniciarPartida.add(jSeparator1);

        miSair.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        miSair.setMnemonic('s');
        miSair.setText("Sair de partida");
        miSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSairActionPerformed(evt);
            }
        });
        miIniciarPartida.add(miSair);

        jMenuBar1.add(miIniciarPartida);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void miIngressarEmPartidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miIngressarEmPartidaActionPerformed
        iniciarJogoExperimental();
    }//GEN-LAST:event_miIngressarEmPartidaActionPerformed

    private void miSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSairActionPerformed
        if (JOptionPane.showConfirmDialog(null,
                "Confirmar saída da partida atual?", "Sair da partida",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                == JOptionPane.YES_OPTION) {
            configuracaoInicial();
        }
    }//GEN-LAST:event_miSairActionPerformed

    private void lMaoJogador1Carta1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lMaoJogador1Carta1MouseClicked

        Carta c;

        boolean seteNaMao = false;
        boolean asNaMao = false;
        boolean seteAsNaMao = false;

        int cartasNaMesa = 0;
        for (int i = 0; i < 4; i++) {
            if (mesaJogador[i] != null) {
                cartasNaMesa++;
                c = mesaJogador[i];
                if (c.getFigura() == 7 && c.getNaipe() == trunfo) {
                    seteLancada = true;
                    rodadaSeteSaiu = numeroRodadas;

                }
            }
        }

        switch (maoJogador[0].size()) {
            case 2:
                if (maoJogador[0].get(0).getFigura() == 7 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(0).getFigura() == 1 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    asNaMao = true;
                }
                if (maoJogador[0].get(1).getFigura() == 7 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(1).getFigura() == 1 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    asNaMao = true;

                }
                break;
            case 3:
                if (maoJogador[0].get(0).getFigura() == 7 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(0).getFigura() == 1 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    asNaMao = true;
                }
                if (maoJogador[0].get(1).getFigura() == 7 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(1).getFigura() == 1 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    asNaMao = true;

                }
                if (maoJogador[0].get(2).getFigura() == 7 && maoJogador[0].get(2).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(2).getFigura() == 1 && maoJogador[0].get(2).getNaipe() == trunfo) {
                    asNaMao = true;

                }
                break;
            default:
                break;

        }

        if (seteNaMao && asNaMao) {
            seteAsNaMao = true;
        }

        c = maoJogador[0].get(0);

        if (jogadorAtual == 0 && (maoJogador[0].size() == 3 && numeroRodadas <= 8
                || maoJogador[0].size() == 2 && numeroRodadas == 9 || maoJogador[0].size() == 1 && numeroRodadas == 10)
                && (c.getFigura() != 1 || c.getNaipe() != trunfo || seteLancada == true || numeroRodadas == 10)
                && (c.getFigura() != 7 || c.getNaipe() != trunfo || cartasNaMesa < 3 || numeroRodadas == 10 || seteAsNaMao)) {

            selecionarCarta(0);

            asSelecionada = false;
            seteSelecionada = false;

        } else if (c.getFigura() == 1 && c.getNaipe() == trunfo && seteLancada == false && jogadorAtual == 0) {

            indCartaPreSelecionada = -1;

            selecionarCarta(0);

            asSelecionada = true;

            seteSelecionada = false;

        } else if (c.getFigura() == 7 && c.getNaipe() == trunfo && cartasNaMesa == 3 && jogadorAtual == 0) {

            if (seteAsNaMao) {
                selecionarCarta(0);

                asSelecionada = false;
                seteSelecionada = false;
            } else {

                indCartaPreSelecionada = -1;

                selecionarCarta(0);

                seteSelecionada = true;

                asSelecionada = false;

            }
        }

    }//GEN-LAST:event_lMaoJogador1Carta1MouseClicked

    private void lMaoJogador1Carta2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lMaoJogador1Carta2MouseClicked

        Carta c;

        boolean seteNaMao = false;
        boolean asNaMao = false;
        boolean seteAsNaMao = false;

        int cartasNaMesa = 0;
        for (int i = 0; i < 4; i++) {
            if (mesaJogador[i] != null) {
                cartasNaMesa++;
                c = mesaJogador[i];
                if (c.getFigura() == 7 && c.getNaipe() == trunfo) {
                    seteLancada = true;
                    rodadaSeteSaiu = numeroRodadas;

                }
            }
        }

        switch (maoJogador[0].size()) {
            case 2:
                if (maoJogador[0].get(0).getFigura() == 7 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(0).getFigura() == 1 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    asNaMao = true;
                }
                if (maoJogador[0].get(1).getFigura() == 7 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(1).getFigura() == 1 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    asNaMao = true;

                }
                break;
            case 3:
                if (maoJogador[0].get(0).getFigura() == 7 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(0).getFigura() == 1 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    asNaMao = true;
                }
                if (maoJogador[0].get(1).getFigura() == 7 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(1).getFigura() == 1 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    asNaMao = true;

                }
                if (maoJogador[0].get(2).getFigura() == 7 && maoJogador[0].get(2).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(2).getFigura() == 1 && maoJogador[0].get(2).getNaipe() == trunfo) {
                    asNaMao = true;

                }
                break;

            default:
                break;

        }

        if (seteNaMao && asNaMao) {
            seteAsNaMao = true;
        }

        c = maoJogador[0].get(1);

        if (jogadorAtual == 0 && (maoJogador[0].size() == 3 && numeroRodadas <= 8
                || maoJogador[0].size() == 2 && numeroRodadas == 9 || maoJogador[0].size() == 1 && numeroRodadas == 10)
                && (c.getFigura() != 1 || c.getNaipe() != trunfo || seteLancada == true)
                && (c.getFigura() != 7 || c.getNaipe() != trunfo || cartasNaMesa < 3 || seteAsNaMao)) {

            selecionarCarta(1);

            asSelecionada = false;
            seteSelecionada = false;

        } else if (c.getFigura() == 1 && c.getNaipe() == trunfo && seteLancada == false && jogadorAtual == 0) {

            indCartaPreSelecionada = -1;

            selecionarCarta(1);

            asSelecionada = true;
            seteSelecionada = false;

        } else if (c.getFigura() == 7 && c.getNaipe() == trunfo && cartasNaMesa == 3 && jogadorAtual == 0) {
            if (seteAsNaMao) {
                selecionarCarta(1);

                asSelecionada = false;
                seteSelecionada = false;
            } else {
                indCartaPreSelecionada = -1;

                selecionarCarta(1);

                seteSelecionada = true;
                asSelecionada = false;

            }
        }

    }//GEN-LAST:event_lMaoJogador1Carta2MouseClicked

    private void lMaoJogador1Carta3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lMaoJogador1Carta3MouseClicked

        Carta c;

        boolean seteNaMao = false;
        boolean asNaMao = false;
        boolean seteAsNaMao = false;

        int cartasNaMesa = 0;

        for (int i = 0; i < 4; i++) {
            if (mesaJogador[i] != null) {
                cartasNaMesa++;
                c = mesaJogador[i];
                if (c.getFigura() == 7 && c.getNaipe() == trunfo) {
                    seteLancada = true;
                    rodadaSeteSaiu = numeroRodadas;

                }
            }

        }

        switch (maoJogador[0].size()) {
            case 2:
                if (maoJogador[0].get(0).getFigura() == 7 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(0).getFigura() == 1 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    asNaMao = true;
                }
                if (maoJogador[0].get(1).getFigura() == 7 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(1).getFigura() == 1 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    asNaMao = true;

                }
                break;
            case 3:
                if (maoJogador[0].get(0).getFigura() == 7 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(0).getFigura() == 1 && maoJogador[0].get(0).getNaipe() == trunfo) {
                    asNaMao = true;
                }
                if (maoJogador[0].get(1).getFigura() == 7 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(1).getFigura() == 1 && maoJogador[0].get(1).getNaipe() == trunfo) {
                    asNaMao = true;

                }
                if (maoJogador[0].get(2).getFigura() == 7 && maoJogador[0].get(2).getNaipe() == trunfo) {
                    seteNaMao = true;
                } else if (maoJogador[0].get(2).getFigura() == 1 && maoJogador[0].get(2).getNaipe() == trunfo) {
                    asNaMao = true;

                }
                break;
            default:
                break;

        }

        if (seteNaMao && asNaMao) {
            seteAsNaMao = true;
        }

        c = maoJogador[0].get(2);

        if (jogadorAtual == 0 && (maoJogador[0].size() == 3 && numeroRodadas <= 8
                || maoJogador[0].size() == 2 && numeroRodadas == 9 || maoJogador[0].size() == 1 && numeroRodadas == 10)
                && (c.getFigura() != 1 || c.getNaipe() != trunfo || seteLancada == true)
                && (c.getFigura() != 7 || c.getNaipe() != trunfo || cartasNaMesa < 3)) {

            selecionarCarta(2);

            asSelecionada = false;
            seteSelecionada = false;

        } else if (c.getFigura() == 1 && c.getNaipe() == trunfo && seteLancada == false && jogadorAtual == 0) {

            indCartaPreSelecionada = -1;

            selecionarCarta(2);

            asSelecionada = true;

            seteSelecionada = false;

        } else if (c.getFigura() == 7 && c.getNaipe() == trunfo && cartasNaMesa == 3 && jogadorAtual == 0) {

            if (seteAsNaMao) {
                selecionarCarta(2);

                asSelecionada = false;
                seteSelecionada = false;
            } else {

                indCartaPreSelecionada = -1;

                selecionarCarta(2);

                seteSelecionada = true;
                asSelecionada = false;

            }
        }

    }//GEN-LAST:event_lMaoJogador1Carta3MouseClicked

    private void miIngressarPartidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miIngressarPartidaActionPerformed
        ingressarPartida();
    }//GEN-LAST:event_miIngressarPartidaActionPerformed

    private void miIniciarPartidaEmRedeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miIniciarPartidaEmRedeActionPerformed
        try {
            iniciarPartidaEmRede();
        } catch (IOException ex) {
            Logger.getLogger(MesaDeJogo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_miIniciarPartidaEmRedeActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MesaDeJogo.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MesaDeJogo.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MesaDeJogo.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MesaDeJogo.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MesaDeJogo().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JLabel lBaralho;
    private javax.swing.JLabel lCartaTrunfo;
    private javax.swing.JLabel lMaoJogador1Carta1;
    private javax.swing.JLabel lMaoJogador1Carta2;
    private javax.swing.JLabel lMaoJogador1Carta3;
    private javax.swing.JLabel lMaoJogador2Carta1;
    private javax.swing.JLabel lMaoJogador2Carta2;
    private javax.swing.JLabel lMaoJogador2Carta3;
    private javax.swing.JLabel lMaoJogador3Carta1;
    private javax.swing.JLabel lMaoJogador3Carta2;
    private javax.swing.JLabel lMaoJogador3Carta3;
    private javax.swing.JLabel lMaoJogador4Carta1;
    private javax.swing.JLabel lMaoJogador4Carta2;
    private javax.swing.JLabel lMaoJogador4Carta3;
    private javax.swing.JLabel lMesaJogador1;
    private javax.swing.JLabel lMesaJogador2;
    private javax.swing.JLabel lMesaJogador3;
    private javax.swing.JLabel lMesaJogador4;
    private javax.swing.JLabel lMonteJogador1;
    private javax.swing.JLabel lMonteJogador2;
    private javax.swing.JLabel lNaipeTrunfo;
    private javax.swing.JLabel lNomeDistribuidor;
    private javax.swing.JLabel lNomeJogador1;
    private javax.swing.JLabel lNomeJogador1Placar;
    private javax.swing.JLabel lNomeJogador2;
    private javax.swing.JLabel lNomeJogador2Placar;
    private javax.swing.JLabel lNomeJogador3;
    private javax.swing.JLabel lNomeJogador3Placar;
    private javax.swing.JLabel lNomeJogador4;
    private javax.swing.JLabel lNomeJogador4Placar;
    private javax.swing.JLabel lNomePrimeiroJogadorRodada;
    private javax.swing.JLabel lPlacarDupla13;
    private javax.swing.JLabel lPlacarDupla24;
    private javax.swing.JMenuItem miIngressarEmPartida;
    private javax.swing.JMenuItem miIngressarPartida;
    private javax.swing.JMenu miIniciarPartida;
    private javax.swing.JMenuItem miIniciarPartidaEmRede;
    private javax.swing.JMenuItem miSair;
    private javax.swing.JPanel pPlacar;
    private javax.swing.JPanel pStatusPartida;
    // End of variables declaration//GEN-END:variables
}
