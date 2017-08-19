import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.TransferHandler;

public class FileMover extends javax.swing.JFrame implements DropTargetListener 
{

    private DefaultListModel listModel = new DefaultListModel();
    private DropTarget dropTarget;
    
    public static void main(String[] args) 
    {
		new FileMover();
	}
    
    /** Creates new Frame of FileMover*/
    public FileMover() 
    {
    	super("Drag and drop test");
        initComponents();
        dropTarget = new DropTarget(list, this);
        list.setModel(listModel);
        list.setDragEnabled(true);
        list.setTransferHandler(new FileTransferHandler());
        this.setAlwaysOnTop( true );
        this.setVisible(true);
    }


    private void initComponents() 
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        r1=new JRadioButton("Copy");    
        r2=new JRadioButton("Cut");   
        r3=new JRadioButton("Default"); 
        bg=new ButtonGroup();    
        bg.add(r1);
        bg.add(r2);
        bg.add(r3);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Files:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jLabel1, gridBagConstraints);
        
        getContentPane().add(r1, gridBagConstraints);
        getContentPane().add(r2, gridBagConstraints);
        getContentPane().add(r3, gridBagConstraints);

        jScrollPane1.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(jScrollPane1, gridBagConstraints);

        pack();
    }

    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList list;
    
    public JRadioButton r1;    
    public JRadioButton r2;   
    public JRadioButton r3;  
    public ButtonGroup bg;    

    public void dragEnter(DropTargetDragEvent arg0) {}

    public void dragOver(DropTargetDragEvent arg0) {}

    public void dropActionChanged(DropTargetDragEvent arg0) {}

    public void dragExit(DropTargetEvent arg0) {
    	for (Object obj: list.getSelectedValues()) {
            listModel.removeElement((File)obj);
        }
    }

    public void drop(DropTargetDropEvent evt) 
    {
        int action = evt.getDropAction();
        evt.acceptDrop(action);
        try {
            Transferable data = evt.getTransferable();
            if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<File> files = (List<File>) data.getTransferData(DataFlavor.javaFileListFlavor);
                for (File file : files) {
                    listModel.addElement(file);
                }
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            evt.dropComplete(true);
        }
    }

    private class FileTransferHandler extends TransferHandler {

        @Override
        protected Transferable createTransferable(JComponent c) {
            JList list = (JList) c;
            List<File> files = new ArrayList<File>();
            for (Object obj: list.getSelectedValues()) {
                files.add((File)obj);
            }
            return new FileTransferable(files);
        }

        @Override
        public int getSourceActions(JComponent c) {
            
            if(r1.isSelected())
            {    
            	return COPY; // to copy    
            }  
            else if(r2.isSelected())
            {    
            	return MOVE;   // to cut
            } 
            else if(r3.isSelected())
            {    
            	return COPY_OR_MOVE; // Default Explorer Behviour 
            } 
            
            return COPY_OR_MOVE;
        }
    }

    private class FileTransferable implements Transferable {

        private List<File> files;

        public FileTransferable(List<File> files) {
            this.files = files;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.javaFileListFlavor);
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return files;
        }
    }
}
