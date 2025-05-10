import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import java.io.*;



public class PacManGame extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U';
        int velocityX = 0, velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            switch (this.direction) {
                case 'U' -> { this.velocityX = 0; this.velocityY = -tileSize / 4; }
                case 'D' -> { this.velocityX = 0; this.velocityY = tileSize / 4; }
                case 'L' -> { this.velocityX = -tileSize / 4; this.velocityY = 0; }
                case 'R' -> { this.velocityX = tileSize / 4; this.velocityY = 0; }
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    class UserInfo {
        String name = "";
        String email = "";
        String age = "";
        int score = 0;
        public String level;

        public String toString() {
            return "Player: " + name + "\nEmail: " + email + "\n Age "+ age + "\nScore: " + score;
        }
    }

    private int rowCount = 21, columnCount = 19, tileSize = 32;
    private int boardWidth = columnCount * tileSize, boardHeight = rowCount * tileSize;

    private Image wallImage, blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX", "X        X        X", "X XX XXX X XXX XX X",
        "X                 X", "X XX X XXXXX X XX X", "X    X       X    X",
        "XXXX XXXX XXXX XXXX", "OOOX X       X XOOO", "XXXX X XXrXX X XXXX",
        "O       bpo       O", "XXXX X XXXXX X XXXX", "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX", "X        X        X", "X XX XXX X XXX XX X",
        "X  X     P     X  X", "XX X X XXXXX X X XX", "X    X   X   X    X",
        "X XXXXXX X XXXXXX X", "X                 X", "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls, foods, ghosts;
    Block pacman;
    Timer gameLoop;
    char[] directions = { 'U', 'D', 'L', 'R' };
    Random random = new Random();
    int score = 0, lives = 3;
    boolean gameOver = false;
    boolean isGameStarted = false;
    UserInfo user = new UserInfo();

    public PacManGame() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        for (Block ghost : ghosts) {
            ghost.updateDirection(directions[random.nextInt(4)]);
        }

        gameLoop = new Timer(50, this);
        showUserForm();
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char ch = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                switch (ch) {
                    case 'X' -> walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                    case 'b' -> ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                    case 'o' -> ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                    case 'p' -> ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                    case 'r' -> ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                    case 'P' -> pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                    case ' ' -> foods.add(new Block(null, x + 14, y + 14, 4, 4));
                   }
            }
        }
    }
    /**
     * 
     */
    public void showUserForm() {
        while (true) {
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField ageField = new JTextField();
    
            // Panel for input fields
            JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            inputPanel.setBackground(new Color(230, 255, 250));
            inputPanel.add(new JLabel("Enter your name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Enter your email:"));
            inputPanel.add(emailField);
            inputPanel.add(new JLabel("Enter your age:"));
            inputPanel.add(ageField);
    
            // Load and scale image
            ImageIcon icon = new ImageIcon("C:\\Users\\DELL\\Downloads\\pacman-java-master\\playerIcon.jpg");
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(img));
            JPanel imagePanel = new JPanel();
            imagePanel.setBackground(new Color(230, 255, 250));
            imagePanel.add(imgLabel);
    
            // Main layout panel
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBackground(new Color(230, 255, 250));
            mainPanel.setPreferredSize(new Dimension(450, 220));
            mainPanel.add(imagePanel, BorderLayout.WEST);
            mainPanel.add(inputPanel, BorderLayout.CENTER);
    
            int option = JOptionPane.showConfirmDialog(
                null,
                mainPanel,
                "Enter Player Info",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
    
            if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                JOptionPane.showMessageDialog(null, "Without user information, you cannot play the game!", "Info Required", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
    
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String ageText = ageField.getText().trim();
    
            if (name.isEmpty() || email.isEmpty() || ageText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields are required!", "Input Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
    
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                JOptionPane.showMessageDialog(null, "Enter a valid email address!", "Input Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
    
            try {
                int ageVal = Integer.parseInt(ageText);
                if (ageVal < 5 || ageVal > 120) {
                    JOptionPane.showMessageDialog(null, "Age must be between 5 and 120!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                user.name = name;
                user.email = email;
                user.age = ageText;
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Age must be a number!", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void showScoreForm(int currentScore) {
        int previousScore = 0;
        File scoreFile = new File("score.txt");
    
        // ✅ 1. Read previous score before updating
        try {
            if (scoreFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(scoreFile));
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    previousScore = Integer.parseInt(line.trim());
                }
                reader.close();
            }
        } catch (IOException | NumberFormatException e) {
            previousScore = 0;
        }
    
        // ✅ 2. UI components
        JTextField feedbackField = new JTextField();
    
        JPanel textPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        textPanel.setBackground(new Color(255, 250, 240));
        textPanel.add(new JLabel("🎮 Game Over!", JLabel.CENTER));
        textPanel.add(new JLabel("Current Score: " + currentScore));
        textPanel.add(new JLabel("Previous High Score: " + previousScore));
        textPanel.add(new JLabel("Any Feedback?"));
        textPanel.add(feedbackField);
    
        // ✅ 3. Load and scale image
        JLabel imgLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("C:\\Users\\DELL\\Downloads\\pacman-java-master\\score.png");
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            imgLabel.setText("Image not found");
        }
    
        JPanel imgPanel = new JPanel();
        imgPanel.setBackground(new Color(255, 250, 240));
        imgPanel.add(imgLabel);
    
        // ✅ 4. Combine image and text
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setPreferredSize(new Dimension(450, 220));
        mainPanel.setBackground(new Color(255, 250, 240));
        mainPanel.add(textPanel, BorderLayout.CENTER);
        mainPanel.add(imgPanel, BorderLayout.EAST);
    
        JOptionPane.showConfirmDialog(
            null,
            mainPanel,
            "Game Summary",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
    
        // ✅ 5. Now update high score if current is higher
        if (currentScore > previousScore) {
            try (FileWriter writer = new FileWriter(scoreFile)) {
                writer.write(String.valueOf(currentScore));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
     
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        for (Block ghost : ghosts) g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        for (Block wall : walls) g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);

        g.setColor(Color.WHITE);
        for (Block food : foods) g.fillRect(food.x, food.y, food.width, food.height);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (!isGameStarted) {
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Press V to Start the Game", tileSize * 4, tileSize * 9);
        } else if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.drawString("GAME OVER", tileSize * 6, tileSize * 7);
            g.drawString("Score: " + score, tileSize * 6, tileSize * 8);
            g.drawString("Player: " + user.name, tileSize * 6, tileSize * 9);
            g.drawString("Email: " + user.email, tileSize * 6, tileSize * 10);
            g.drawString("Press V to Restart | R to Exit", tileSize * 3, tileSize * 12);
        } else {
            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, tileSize / 2, tileSize / 2);
            g.drawString("Lives: " + "❤".repeat(lives), tileSize * 12, tileSize / 2);
        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                lives--;
                if (lives == 0) {
                    gameOver = true;
                    gameLoop.stop();
                    user.score = score;
                    // Show final score form
                    showScoreForm(score);
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x &&
               a.y < b.y + b.height && a.y + a.height > b.y;
    }

    public void resetPositions()
     {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
    }

    @Override public void actionPerformed(ActionEvent e) {
        if (!isGameStarted || gameOver) return;
        move();
        repaint();
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (!isGameStarted) {
            if (key == KeyEvent.VK_V) {
                isGameStarted = true;
                gameLoop.start();
            }
            repaint();
            return;
        }

        if (gameOver) {
            if (key == KeyEvent.VK_V) {
                loadMap();
                resetPositions();
                lives = 3;
                score = 0;
                gameOver = false;
                gameLoop.start();
            } else if (key == KeyEvent.VK_R) {
                System.exit(0);
            }
            repaint();
            return;
        }

        switch (key) {
            case KeyEvent.VK_UP -> {
                pacman.updateDirection('U');
                pacman.image = pacmanUpImage;
            }
            case KeyEvent.VK_DOWN -> {
                pacman.updateDirection('D');
                pacman.image = pacmanDownImage;
            }
            case KeyEvent.VK_LEFT -> {
                pacman.updateDirection('L');
                pacman.image = pacmanLeftImage;
            }
            case KeyEvent.VK_RIGHT -> {
                pacman.updateDirection('R');
                pacman.image = pacmanRightImage;
            }
        }
    }

    // Main method
    public static void main(String[] args) {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacManGame pacmanGame = new PacManGame();
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);
    }
}
