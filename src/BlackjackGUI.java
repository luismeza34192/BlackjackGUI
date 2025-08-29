import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlackjackGUI extends JFrame {
    private List<String> deck;
    private List<String> playerHand;
    private List<String> dealerHand;
    private int chips = 100;
    private int bet = 0;

    private JTextArea gameLog;
    private JButton hitButton, standButton, betButton;
    private JTextField betField;

    public BlackjackGUI() {
        setTitle("Blackjack");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gameLog = new JTextArea();
        gameLog.setEditable(false);
        add(new JScrollPane(gameLog), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");
        betButton = new JButton("Place Bet");
        betField = new JTextField("10", 5);

        controlPanel.add(new JLabel("Bet:"));
        controlPanel.add(betField);
        controlPanel.add(betButton);
        controlPanel.add(hitButton);
        controlPanel.add(standButton);
        add(controlPanel, BorderLayout.SOUTH);

        hitButton.setEnabled(false);
        standButton.setEnabled(false);

        betButton.addActionListener(e -> placeBet());
        hitButton.addActionListener(e -> playerHits());
        standButton.addActionListener(e -> playerStands());

        startNewRound();
    }

    private void startNewRound() {
        deck = createDeck();
        Collections.shuffle(deck);
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();

        playerHand.add(drawCard());
        playerHand.add(drawCard());
        dealerHand.add(drawCard());
        dealerHand.add(drawCard());

        gameLog.setText("New round!\nChips: " + chips + "\n");
        gameLog.append("Dealer shows: " + dealerHand.get(0) + "\n");
        gameLog.append("Your hand: " + playerHand + " (Total: " + handValue(playerHand) + ")\n");

        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        betButton.setEnabled(true);
    }

    private void placeBet() {
        try {
            bet = Integer.parseInt(betField.getText());
            if (bet <= 0 || bet > chips) {
                gameLog.append("Invalid bet amount.\n");
                return;
            }
            gameLog.append("Bet placed: " + bet + "\n");
            hitButton.setEnabled(true);
            standButton.setEnabled(true);
            betButton.setEnabled(false);
        } catch (NumberFormatException ex) {
            gameLog.append("Please enter a valid number.\n");
        }
    }

    private void playerHits() {
        playerHand.add(drawCard());
        gameLog.append("You drew: " + playerHand.get(playerHand.size() - 1) + "\n");
        gameLog.append("Your hand: " + playerHand + " (Total: " + handValue(playerHand) + ")\n");

        if (handValue(playerHand) > 21) {
            gameLog.append("You bust! Dealer wins.\n");
            chips -= bet;
            endRound();
        }
    }

    private void playerStands() {
        gameLog.append("You stand.\n");
        gameLog.append("Dealer's hand: " + dealerHand + " (Total: " + handValue(dealerHand) + ")\n");

        while (handValue(dealerHand) < 17 ||
                (handValue(dealerHand) < handValue(playerHand) && handValue(playerHand) <= 21)) {
            String card = drawCard();
            dealerHand.add(card);
            gameLog.append("Dealer draws: " + card + "\n");
        }

        int playerTotal = handValue(playerHand);
        int dealerTotal = handValue(dealerHand);

        gameLog.append("Final hands:\n");
        gameLog.append("Your hand: " + playerHand + " (Total: " + playerTotal + ")\n");
        gameLog.append("Dealer's hand: " + dealerHand + " (Total: " + dealerTotal + ")\n");

        if (dealerTotal > 21 || playerTotal > dealerTotal) {
            gameLog.append("You win!\n");
            chips += bet;
        } else if (playerTotal < dealerTotal) {
            gameLog.append("Dealer wins.\n");
            chips -= bet;
        } else {
            gameLog.append("It's a tie.\n");
        }

        endRound();
    }

    private void endRound() {
        gameLog.append("Chips remaining: " + chips + "\n");
        hitButton.setEnabled(false);
        standButton.setEnabled(false);

        if (chips <= 0) {
            gameLog.append("You're out of chips! Game over.\n");
            betButton.setEnabled(false);
        } else {
            int result = JOptionPane.showConfirmDialog(this, "Play another round?", "Continue?", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                startNewRound();
            } else {
                gameLog.append("Thanks for playing!\n");
            }
        }
    }

    private List<String> createDeck() {
        String[] suits = {"♠", "♥", "♦", "♣"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        List<String> newDeck = new ArrayList<>();
        for (String suit : suits) {
            for (String rank : ranks) {
                newDeck.add(rank + suit);
            }
        }
        return newDeck;
    }

    private String drawCard() {
        return deck.remove(0);
    }

    private int handValue(List<String> hand) {
        int value = 0;
        int aces = 0;
        for (String card : hand) {
            String rank = card.substring(0, card.length() - 1);
            if (rank.equals("A")) {
                value += 11;
                aces++;
            } else if ("KQJ".contains(rank)) {
                value += 10;
            } else {
                value += Integer.parseInt(rank);
            }
        }
        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }
        return value;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BlackjackGUI().setVisible(true));
    }
}