import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class Gui extends JFrame 
{
    private JPanel panel1, panel2, panel3, panel4;
    private File[] files = new File[4]; // Store files for each panel

    public Gui() 
    {
        setTitle("File Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setVisible(true);

        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel1 = createSelectPanel(0);
        panel2 = createSelectPanel(1);
        panel3 = createSelectPanel(2);
        panel4 = createSelectPanel(3);

        mainPanel.add(panel1);
        mainPanel.add(panel2);
        mainPanel.add(panel3);
        mainPanel.add(panel4);

        add(mainPanel);
    }

    private JPanel createSelectPanel(int panelIndex) 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        panel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.BLACK);
        
        JLabel label = new JLabel("Select a file");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.LIGHT_GRAY);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        
        contentPanel.add(label, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Mouse listener
        panel.addMouseListener(new java.awt.event.MouseAdapter() 
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) 
            {
                openFileChooserForPanel(panelIndex, panel);
            }
        });
        
        return panel;
    }

    private void openFileChooserForPanel(int panelIndex, JPanel panel) 
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a file for Panel " + (panelIndex + 1));
        
        // File filters
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Image Files (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg");
        FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf");
        FileNameExtensionFilter videoFilter = new FileNameExtensionFilter("Video Files (*.mp4, *.avi, *.mov)", "mp4", "avi", "mov");
        FileNameExtensionFilter allFilter = new FileNameExtensionFilter("All Supported Files", "png", "jpg", "jpeg", "pdf", "mp4", "avi", "mov");

        fileChooser.addChoosableFileFilter(allFilter);
        fileChooser.addChoosableFileFilter(imageFilter);
        fileChooser.addChoosableFileFilter(pdfFilter);
        fileChooser.addChoosableFileFilter(videoFilter);
        fileChooser.setFileFilter(allFilter);
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) 
        {
            File selectedFile = fileChooser.getSelectedFile();
            files[panelIndex] = selectedFile;
            displayFile(panelIndex, panel, selectedFile);
        }
    }

    private void displayFile(int panelIndex, JPanel panel, File file) 
    {
        String extension = getFileExtension(file.getName()).toLowerCase();
        
        panel.removeAll();
        
        switch (extension) 
        {
            case "png":
            case "jpg":
            case "jpeg":
                displayImageInPanel(panel, file);
                break;
            case "pdf":
                displayPDFInPanel(panel, file, panelIndex);
                break;
            case "mp4":
            case "avi":
            case "mov":
                displayVideoInPanel(panel, file, panelIndex);
                break;
            default:
                displayErrorInPanel(panel, "Unsupported file type: " + extension, panelIndex);
        }
        
        panel.revalidate();
        panel.repaint();
    }

    private void displayImageInPanel(JPanel panel, File file) 
    {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.BLACK);
        
        try {
            BufferedImage img = ImageIO.read(file);
            if (img != null) {
                Image scaledImage = img.getScaledInstance(350, 300, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                contentPanel.add(imageLabel, BorderLayout.CENTER);
            }
        }
        catch (Exception e) 
        {
            JLabel errorLabel = new JLabel("Error loading image: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            contentPanel.add(errorLabel, BorderLayout.CENTER);
        }
        
        panel.add(contentPanel, BorderLayout.CENTER);
    }

    private void displayPDFInPanel(JPanel panel, File file, int panelIndex) 
    {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.BLACK);
        
        try {
            if (file.exists()) 
            {
                PDDocument document = PDDocument.load(file);
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                
                // Render first page
                BufferedImage pdfImage = pdfRenderer.renderImageWithDPI(0, 100);
                Image scaledImage = pdfImage.getScaledInstance(350, 300, Image.SCALE_SMOOTH);
                JLabel pdfLabel = new JLabel(new ImageIcon(scaledImage));
                
                contentPanel.add(pdfLabel, BorderLayout.CENTER);
                
                document.close();
            } 
            else 
            {
                JLabel errorLabel = new JLabel("PDF file not found");
                errorLabel.setForeground(Color.RED);
                contentPanel.add(errorLabel, BorderLayout.CENTER);
            }
        }
        catch (Exception e) 
        {
            JLabel errorLabel = new JLabel("<html>Error: " + e.getMessage() + "</html>");
            errorLabel.setForeground(Color.RED);
            contentPanel.add(errorLabel, BorderLayout.CENTER);
        }
        
        panel.add(contentPanel, BorderLayout.CENTER);
    }

    private void displayVideoInPanel(JPanel panel, File file, int panelIndex) 
    {
        try {
            if (file.exists()) 
            {
                // Create JavaFX MediaPlayer panel
                JFXPanel jfxPanel = new JFXPanel();
                
                javafx.application.Platform.runLater(() -> {
                    try {
                        // Media and player
                        String videoPath = file.toURI().toString();
                        Media media = new Media(videoPath);
                        MediaPlayer mediaPlayer = new MediaPlayer(media);
                        
                        // MediaView
                        MediaView mediaView = new MediaView(mediaPlayer);
                        mediaView.setPreserveRatio(true);
                        mediaView.setFitWidth(350);
                        mediaView.setFitHeight(250);
                        
                        // Loop and Auto-play
                        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                         
                        mediaPlayer.play();
                        
                        // Layout
                        VBox root = new VBox(10);
                        root.setAlignment(Pos.CENTER);
                        root.setStyle("-fx-background-color: #333333;");
                        root.getChildren().add(mediaView);
                        
                        Scene scene = new Scene(root, 350, 300);
                        jfxPanel.setScene(scene);
                    } 
                    catch (Exception e) 
                    {
                        e.printStackTrace();
                    }
                });
                
                panel.add(jfxPanel, BorderLayout.CENTER);
            } 
            else 
            {
                JPanel contentPanel = new JPanel();
                contentPanel.setBackground(Color.BLACK);
                
                JLabel errorLabel = new JLabel("Video file not found");
                errorLabel.setForeground(Color.RED);
                contentPanel.add(errorLabel, BorderLayout.CENTER);
                
                panel.add(contentPanel, BorderLayout.CENTER);
            }
        }
        catch (Exception e) 
        {
            JPanel contentPanel = new JPanel();
            contentPanel.setBackground(Color.BLACK);
            
            JLabel errorLabel = new JLabel("<html>Error: " + e.getMessage() + "</html>");
            errorLabel.setForeground(Color.RED);
            contentPanel.add(errorLabel, BorderLayout.CENTER);
            
            panel.add(contentPanel, BorderLayout.CENTER);
        }
    }

    private void displayErrorInPanel(JPanel panel, String errorMessage, int panelIndex) 
    {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.BLACK);
        
        JLabel errorLabel = new JLabel("<html>" + errorMessage + "</html>");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(errorLabel, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
    }

    private String getFileExtension(String fileName) 
    {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
    }

    public static void main(String A[]) 
    {
        SwingUtilities.invokeLater(Gui::new);
    }
}