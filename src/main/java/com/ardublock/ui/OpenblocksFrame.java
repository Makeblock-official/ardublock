package com.ardublock.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ardublock.core.Context;
import com.ardublock.ui.listener.ArdublockWorkspaceListener;
import com.ardublock.ui.listener.GenerateCodeButtonListener;
import com.ardublock.ui.listener.NewButtonListener;
import com.ardublock.ui.listener.OpenButtonListener;
import com.ardublock.ui.listener.OpenblocksFrameListener;
import com.ardublock.ui.listener.SaveAsButtonListener;
import com.ardublock.ui.listener.SaveButtonListener;

import edu.mit.blocks.controller.WorkspaceController;
import edu.mit.blocks.workspace.Workspace;


public class OpenblocksFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2841155965906223806L;

	private Context context;
	private JFileChooser fileChooser;
	private FileFilter ffilter;
	
	private ResourceBundle uiMessageBundle;
	
	public void addListener(OpenblocksFrameListener ofl)
	{
		context.registerOpenblocksFrameListener(ofl);
		
	}
	
	public String makeFrameTitle()
	{
		String title = Context.APP_NAME + " " + context.getSaveFileName();
		if (context.isWorkspaceChanged())
		{
			title = title + " *";
		}
		return title;
		
	}
	
	public OpenblocksFrame()
	{
		context = Context.getContext();
		this.setTitle(makeFrameTitle());
		this.setSize(new Dimension(1024, 760));
		this.setLayout(new BorderLayout());
		//put the frame to the center of screen
		this.setLocationRelativeTo(null);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
		
		fileChooser = new JFileChooser();
		ffilter = new FileNameExtensionFilter(uiMessageBundle.getString("ardublock.file.suffix"), "abp");
		fileChooser.setFileFilter(ffilter);
		fileChooser.addChoosableFileFilter(ffilter);
		
		initOpenBlocks();
	}
	
	private void initOpenBlocks()
	{
		final Context context = Context.getContext();
		
		/*
		WorkspaceController workspaceController = context.getWorkspaceController();
		JComponent workspaceComponent = workspaceController.getWorkspacePanel();
		*/
		
		final Workspace workspace = context.getWorkspace();
		
		// WTF I can't add worksapcelistener by workspace contrller
		workspace.addWorkspaceListener(new ArdublockWorkspaceListener(this));
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		JButton newButton = new JButton(uiMessageBundle.getString("ardublock.ui.new"));
		newButton.addActionListener(new NewButtonListener(this));
		JButton saveButton = new JButton(uiMessageBundle.getString("ardublock.ui.save"));
		saveButton.addActionListener(new SaveButtonListener(this));
		JButton saveAsButton = new JButton(uiMessageBundle.getString("ardublock.ui.saveAs"));
		saveAsButton.addActionListener(new SaveAsButtonListener(this));
		JButton openButton = new JButton(uiMessageBundle.getString("ardublock.ui.load"));
		openButton.addActionListener(new OpenButtonListener(this));
		JButton generateButton = new JButton(uiMessageBundle.getString("ardublock.ui.upload"));
		generateButton.addActionListener(new GenerateCodeButtonListener(this, context));
		JButton serialMonitorButton = new JButton(uiMessageBundle.getString("ardublock.ui.serialMonitor"));
		serialMonitorButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				context.getEditor().handleSerial();
			}
		});
		JButton saveImageButton = new JButton(uiMessageBundle.getString("ardublock.ui.saveImage"));
		saveImageButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				Dimension size = workspace.getCanvasSize();
				System.out.println("size: " + size);
				BufferedImage bi = new BufferedImage(2560, 2560, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D)bi.createGraphics();
				double theScaleFactor = (300d/72d);  
				g.scale(theScaleFactor,theScaleFactor);
				
				workspace.getBlockCanvas().getPageAt(0).getJComponent().paint(g);
				try{
					final JFileChooser fc = new JFileChooser();
					fc.setSelectedFile(new File("ardublock.png"));
					int returnVal = fc.showSaveDialog(workspace.getBlockCanvas().getJComponent());
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
						ImageIO.write(bi,"png",file);
			        }
				} catch (Exception e1) {
					
				} finally {
					g.dispose();
				}
			}
		});

		buttons.add(newButton);
		buttons.add(saveButton);
		buttons.add(saveAsButton);
		buttons.add(openButton);
		buttons.add(generateButton);
		buttons.add(serialMonitorButton);

		JPanel bottomPanel = new JPanel();
		JButton websiteButton = new JButton(uiMessageBundle.getString("ardublock.ui.website"));
		websiteButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
			    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    URL url;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			        	Locale locale = Locale.getDefault();  
			        	if(locale.getLanguage().equals("zh")){
			        		url = new URL("http://bbs.makeblock.cc/?from=ardublock");
			        	}else{
			        		url = new URL("http://forum.makeblock.cc/#ardublock");
			        	}
			            desktop.browse(url.toURI());
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        }
			    }
			}
		});
		JButton checkButton = new JButton(uiMessageBundle.getString("ardublock.ui.checkupdate"));
		checkButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				try {
					parseXml("https://raw.githubusercontent.com/Makeblock-official/ardublock/master/config/version.xml");
		        } catch (Exception e1) {
		            e1.printStackTrace();
		        }
			}
		});
		
		JLabel versionLabel = new JLabel("v " + uiMessageBundle.getString("ardublock.ui.version"));
		
		bottomPanel.add(saveImageButton);
		bottomPanel.add(checkButton);
		bottomPanel.add(websiteButton);
		bottomPanel.add(versionLabel);

		
		this.add(buttons, BorderLayout.NORTH);
		this.add(bottomPanel, BorderLayout.SOUTH);
		this.add(workspace, BorderLayout.CENTER);
	}
	private String lastVersion = null;
	private String lastFile = null;
	public void parseXml(String fileName) {
	       try {
	           DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	           DocumentBuilder builder = factory.newDocumentBuilder();
	           Document document = builder.parse(fileName);
	           // 1.获得文档根元素对对象;
	           Element root = document.getDocumentElement();
	           // 获得文档根元素下一级子元素所有元素;
	           NodeList nodeList = root.getChildNodes();
	           System.out.println(root.getNodeName());
	           if (null != root) {
	              for (int i = 0; i < nodeList.getLength(); i++) {
	                  Node child = nodeList.item(i);
	                  for (Node node = child.getFirstChild(); node != null; node = node .getNextSibling()) {
		                  if (node.getNodeType() == Node.ELEMENT_NODE) {
		                       if ("file".equals(node.getNodeName())) {
		                    	   lastFile = node.getFirstChild().getNodeValue();
		                       }else if ("version".equals(node.getNodeName())) {
		                    	   lastVersion=node.getFirstChild().getNodeValue();
			                   }
		                   }
	                  }
	              }
	              JDialog dialog = new JDialog();
        		  dialog.setTitle(uiMessageBundle.getString("ardublock.ui.checkupdate"));
        		  dialog.setBounds(this.getBounds().x+this.getBounds().width/2-100, this.getBounds().y+this.getBounds().height/2-50, 280, 140);
        		  dialog.setVisible(true);
        		  dialog.setResizable(false);
        		  JLabel label = new JLabel();
        		  label.setHorizontalAlignment(JLabel.CENTER);
        		  JButton updateButton = new JButton(uiMessageBundle.getString("ardublock.ui.updatenow"));
        			updateButton.addActionListener(new ActionListener () {
        				public void actionPerformed(ActionEvent e) {
        					Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        				    URL url;
        				    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        				        try {
        							url = new URL(lastFile);
        				            desktop.browse(url.toURI());
        				        } catch (Exception e1) {
        				            e1.printStackTrace();
        				        }
        				    }
        				}
        			});
          			Container pane = dialog.getContentPane();
					if(lastVersion!=null&&lastFile!=null){
						  if(!lastVersion.equals(uiMessageBundle.getString("ardublock.ui.version"))){
							label.setText(uiMessageBundle.getString("ardublock.ui.versionmessage"));
							pane.add(updateButton);
			        		  label.setBounds(18, 20, 240, 40);
						  }else{
							  label.setText(uiMessageBundle.getString("ardublock.ui.noversionmessage"));
			        		  label.setBounds(18, 30, 240, 40);
						  }
						  System.out.print("version:"+lastVersion+" current:"+uiMessageBundle.getString("ardublock.ui.version")+"\r\n");
						  System.out.print("file:"+lastFile);
					}else{
		        		  label.setBounds(18, 30, 240, 40);
						  label.setText(uiMessageBundle.getString("ardublock.ui.noversionmessage"));
					}
							
	      			pane.setLayout(null);
	      			updateButton.setBounds(50, 66, 100, 28);
	      			pane.add(label);
	           }
	       } catch (ParserConfigurationException e) {

	           e.printStackTrace();

	       } catch (IOException e) {

	           e.printStackTrace();

	       } catch (SAXException e) {

	           e.printStackTrace();

	       }

	    }
	public void savexmlUrl(String xmlUrl) {
		   try {
		    URL url = new URL(xmlUrl);
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.connect();
		    InputStream stream = conn.getInputStream();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(
		      stream, "UTF-8"));
		    StringBuffer document = new StringBuffer();
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		     document.append(line);
		    }
		    System.out.println(document);
		   }catch(Exception e){
			   
		   }
	}
	public void doOpenArduBlockFile()
	{
		if (context.isWorkspaceChanged())
		{
			int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.content.open_unsaved"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if (optionValue == JOptionPane.YES_OPTION)
			{
				doSaveArduBlockFile();
				this.loadFile();
			}
			else
			{
				if (optionValue == JOptionPane.NO_OPTION)
				{
					this.loadFile();
				}
			}
		}
		else
		{
			this.loadFile();
		}
		this.setTitle(makeFrameTitle());
	}
	
	private void loadFile()
	{
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File savedFile = fileChooser.getSelectedFile();
			if (!savedFile.exists())
			{
				JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.file_not_found"), uiMessageBundle.getString("message.title.error"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, JOptionPane.OK_OPTION);
				return ;
			}
			
			try
			{
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				context.loadArduBlockFile(savedFile);
				context.setWorkspaceChanged(false);
			}
			catch (IOException e)
			{
				JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.file_not_found"), uiMessageBundle.getString("message.title.error"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, JOptionPane.OK_OPTION);
				e.printStackTrace();
			}
			finally
			{
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
	
	public void doSaveArduBlockFile()
	{
		if (!context.isWorkspaceChanged())
		{
			return ;
		}
		
		String saveString = getArduBlockString();
		
		if (context.getSaveFilePath() == null)
		{
			chooseFileAndSave(saveString);
		}
		else
		{
			File saveFile = new File(context.getSaveFilePath());
			writeFileAndUpdateFrame(saveString, saveFile);
		}
	}

	
	public void doSaveAsArduBlockFile()
	{
		if (context.isWorkspaceEmpty())
		{
			return ;
		}
		
		String saveString = getArduBlockString();
		
		chooseFileAndSave(saveString);
		
	}
	
	private void chooseFileAndSave(String ardublockString)
	{
		File saveFile = letUserChooseSaveFile();
		saveFile = checkFileSuffix(saveFile);
		if (saveFile == null)
		{
			return ;
		}
		
		if (saveFile.exists() && !askUserOverwriteExistedFile())
		{
			return ;
		}
		
		writeFileAndUpdateFrame(ardublockString, saveFile);
	}
	
	private String getArduBlockString()
	{
		WorkspaceController workspaceController = context.getWorkspaceController();
		return workspaceController.getSaveString();
	}
	
	private void writeFileAndUpdateFrame(String ardublockString, File saveFile) 
	{
		try
		{
			saveArduBlockToFile(ardublockString, saveFile);
			context.setWorkspaceChanged(false);
			this.setTitle(this.makeFrameTitle());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private File letUserChooseSaveFile()
	{
		int chooseResult;
		chooseResult = fileChooser.showSaveDialog(this);
		if (chooseResult == JFileChooser.APPROVE_OPTION)
		{
			return fileChooser.getSelectedFile();
		}
		return null;
	}
	
	private boolean askUserOverwriteExistedFile()
	{
		int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.content.overwrite"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
		return (optionValue == JOptionPane.YES_OPTION);
	}
	
	private void saveArduBlockToFile(String ardublockString, File saveFile) throws IOException
	{
		context.saveArduBlockFile(saveFile, ardublockString);
		context.setSaveFileName(saveFile.getName());
		context.setSaveFilePath(saveFile.getAbsolutePath());
	}
	
	public void doNewArduBlockFile()
	{
		if (context.isWorkspaceChanged())
		{
			int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.question.newfile_on_workspace_changed"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if (optionValue != JOptionPane.YES_OPTION)
			{
				return ;
			}
		}
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		context.resetWorksapce();
		context.setWorkspaceChanged(false);
		this.setTitle(this.makeFrameTitle());
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	
	
	private File checkFileSuffix(File saveFile)
	{
		String filePath = saveFile.getAbsolutePath();
		if (filePath.endsWith(".abp"))
		{
			return saveFile;
		}
		else
		{
			return new File(filePath + ".abp");
		}
	}
}
