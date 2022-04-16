import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Main {

    // main method
    public static void main(String[] args) {

        new UI();

    }
    public static void shrink_pdf(File filePath, int reductionSize) throws IOException {

        // create images from document pages
        PDDocument document = PDDocument.load(new File(String.valueOf(filePath)));

        PDDocument newPdf = new PDDocument();

        PDFRenderer pdfRenderer = new PDFRenderer(document);



        int pageNumber;
        // page -> img

        for (pageNumber = 0; pageNumber < document.getNumberOfPages(); ++pageNumber) {

            // render image with selected dpi setting
            //
            BufferedImage img = pdfRenderer.renderImageWithDPI(pageNumber, reductionSize, ImageType.RGB);

            PDImageXObject pdImage = JPEGFactory.createFromImage(document, img, 0.1f);

            float width = img.getWidth();
            float height = img.getHeight();

            PDPage newPage = new PDPage(new PDRectangle(width, height));

            PDPageContentStream contentStream = new PDPageContentStream(newPdf, newPage);


            contentStream.drawImage(pdImage, 0, 0);
            contentStream.close();

            newPdf.addPage(newPage);
            System.out.println("added" + pageNumber);

        }

        newPdf.save("Shrunk.pdf");
        document.close();
        newPdf.close();

    }
    // ui
    public static class UI {
        public UI() {
            // create new frame instance
            JFrame uiFrame = new JFrame("PDF Shrinker");
            uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // create buttons
            JButton uiButton1 = new JButton("Select file");
            JButton uiButton2 = new JButton("Shrink PDF");


            // labels
            JLabel label1 = new JLabel();
            label1.setText("No file selected...");

            // set button location
            label1.setBounds(150, 20, 300, 40);

            // pos
            uiButton1.setBounds(40, 20, 100, 30);
            uiButton2.setBounds(200, 250, 200, 40);

            // radio buttons for quality
            JLabel label2 = new JLabel();
            label2.setText("Select output PDF quality");
            label2.setBounds(40, 70, 150, 40);

            JRadioButton quality1 = new JRadioButton("Low (150 DPI)");
            JRadioButton quality2 = new JRadioButton("Medium (300 DPI)");
            JRadioButton quality3 = new JRadioButton("High (600 DPI)");

            quality1.setBounds(40, 100, 200, 40);
            quality2.setBounds(40, 140, 200, 40);
            quality3.setBounds(40, 180, 200, 40);

            quality1.setActionCommand("150");
            quality2.setActionCommand("300");
            quality3.setActionCommand("600");

            // quality selection
            ButtonGroup buttons = new ButtonGroup();
            buttons.add(quality1);
            buttons.add(quality2);
            buttons.add(quality3);

            // add buttons to frame
            uiFrame.add(uiButton1);
            uiFrame.add(uiButton2);
            uiButton2.setEnabled(false);

            uiFrame.add(label1);
            uiFrame.add(label2);




            // add buttons to frame
            uiFrame.getContentPane().add(quality1);
            uiFrame.getContentPane().add(quality2);
            uiFrame.getContentPane().add(quality3);

            // select default quality
            quality2.setSelected(true);

            // progress bar
//            JProgressBar progressBar = new JProgressBar();
//            progressBar.setValue(0);
//            progressBar.setVisible(false);
//            progressBar.setBounds(200, 300, 200, 30);
//            uiFrame.add(progressBar);

            // frame configs
            uiFrame.setSize(600, 400);
            uiFrame.setLayout(null);
            uiFrame.setVisible(true);
            uiFrame.setLocationRelativeTo(null);
            // dialog options
            JFileChooser chooseFile = new JFileChooser();

            // button press action
            uiButton1.addActionListener(e -> {

                        // filter PDF files
                        chooseFile.setFileFilter(new FileFilter() {
                            @Override
                            public boolean accept(File f) {
                                if (f.isDirectory()) {
                                    return true;
                                } else {
                                    String filename = f.getName().toLowerCase();
                                    return filename.endsWith(".pdf");
                                }
                            }

                            @Override
                            public String getDescription() {
                                return "PDF files (*.pdf)";
                            }
                        });

                        int option = chooseFile.showOpenDialog(null);

                        if (option == JFileChooser.APPROVE_OPTION) {

                            label1.setText(String.valueOf(chooseFile.getSelectedFile()));


                            uiButton2.setEnabled(true);

                        } else {
                            label1.setText("No file chosen");

                        }
                    }
            );

            uiButton2.addActionListener(e -> {

                try {
                    String quality = buttons.getSelection().getActionCommand();


                    shrink_pdf(chooseFile.getSelectedFile(), Integer.parseInt(quality));

//
                    JOptionPane.showMessageDialog(uiFrame,"Done.","PDF Shrunk",JOptionPane.PLAIN_MESSAGE);


                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

}


