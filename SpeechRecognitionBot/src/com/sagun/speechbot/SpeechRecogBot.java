/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package com.sagun.speechbot;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class SpeechRecogBot {

	static String resultText;
	static String answerToStr;
	static String textToSpeech;
	static Process pro;
	static Random ran = new Random();
    public static void main(String[] args) throws InstantiationException {
  
    	
    	
//-----------------------------| GUI COMPONENTS |-----------------------------------------//	
    	/* Main Container */
    	JFrame SpeechFrame=new JFrame("Speech Recognition Bot");
    	SpeechFrame.setSize(480,530);
    	SpeechFrame.setLocationRelativeTo(null);									
		SpeechFrame.setResizable(false); 
		SpeechFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel mainBackgoundImage=new JLabel ("");								
		SpeechFrame.add(mainBackgoundImage);										
		mainBackgoundImage.setIcon(new ImageIcon ("data"+File.separator+"bg.data")); 
		SpeechFrame.setVisible(true);
		mainBackgoundImage.setOpaque(false);
		
		/* Info Button */
		JButton btnInfo=new JButton(new ImageIcon ("data"+File.separator+File.separator+"info.data"));
		mainBackgoundImage.add(btnInfo);
		btnInfo.setRolloverIcon(new ImageIcon ("data"+File.separator+File.separator+"info_actv.data"));
		btnInfo.setBounds(40,210,80,80);
		btnInfo.setToolTipText("About This Application");
		btnInfo.setBorderPainted(false);
		
		// Action Listener for Info Button
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
	        JOptionPane.showMessageDialog(null, "<html><center><h3>Speech Recognition Bot</h3></center><br/>Project to demonstrate Final Year report <br/> for an Undergraduate research on Speech <br/>  Recognition Technology and Sphinx-4<br/><br/><center><b>Disclaimer</b></center> All of the materials used in this application are <br/>either based on Creative Commons license <br/> or are Open Source. Neither NCC Education, <br> nor I, nor the University of Central Lancashire <br> should be held responsible for the unintended <br/> use of this app. <br/> Java is trademark of Oracle and its affiliates.<br/> Sphinx-4 is research of Carnegie Mellon University <br/>and its affiliates and other contents or libraries<br/> used in this app are property of it's respective owners <br/> <br/><b>Sagun Ghimire</b><br/> <b>NCC Student ID :</b> 00126108 </b><br/> <b>UCLAN Student ID :</b> G20654339 </html>");
	            }
	        });
		
		/* How to use Button */
		JButton btnHowToUse=new JButton(new ImageIcon ("data"+File.separator+File.separator+"help.data"));
		mainBackgoundImage.add(btnHowToUse);
		btnHowToUse.setRolloverIcon(new ImageIcon ("data"+File.separator+File.separator+"help_actv.data"));
		btnHowToUse.setBounds(130,210,80,80);
		btnHowToUse.setToolTipText("How To Use");
		btnHowToUse.setBorderPainted(false);
		
		// Action Listener for How to use Button
		btnHowToUse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			   JOptionPane.showMessageDialog(null, "<html> <h3> HOW TO USE </h3> Start the application and u are ready to go<br/> Use commands like: <br><i><ul><li>Hey Computer</li><li> Do you have a name?</li><li> Open *Application Name*</li><li>What does the fox say?</li><li> Make me a coffee</li> </ul>for more commands and troubleshooting guide <br/> please refer to  <b>cheatsheet</b> heading <br/>at User Manual of this application.<br/> </html>");
			       }
			   });
		
		/* Speech Panel or Output window */
		
		//Pane to display users qryDisps
				JTextArea userSpeechDisp=new JTextArea("");
				mainBackgoundImage.add(userSpeechDisp);
				//userSpeechDisp.setLineWrap(true);
				//userSpeechDisp.setWrapStyleWord(true);
				userSpeechDisp.setBounds(48,290,390,65);
				userSpeechDisp.setForeground(new Color(241, 144, 0));
				userSpeechDisp.setFont(new Font("Cambria" , Font.BOLD, 24));
				Border border = BorderFactory.createLineBorder(new Color(0, 84, 186));
				userSpeechDisp.setBorder(BorderFactory.createCompoundBorder(border, 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
				userSpeechDisp.setEditable(false);
				
			//Pane to display Solution or answers
				JTextArea slnDisp=new JTextArea("");
				mainBackgoundImage.add(slnDisp);
				slnDisp.setLineWrap(true);
				slnDisp.setWrapStyleWord(true);
				slnDisp.setForeground(new Color(241, 144, 0));
				slnDisp.setFont(new Font("Cambria" , Font.BOLD, 24));
				slnDisp.setBounds(48,370,390,65);
				slnDisp.setBorder(BorderFactory.createCompoundBorder(border, 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
				slnDisp.setEditable(false);
				
				// Listening Notification
				JLabel listening=new JLabel("");
				listening.setForeground(Color.RED);
				listening.setFont(new Font("Cambria" , Font.BOLD, 24));
				mainBackgoundImage.add(listening);
				listening.setBounds(30,180,290,30);
				listening.setText("Loading");
				
							
//--------------------------------| SPEECH RECOGNITION COMPONENTS |-----------------------------------------//	
		
//Loading Grammars and xml config files
	ConfigurationManager cm;
if (args.length > 0) {
		cm = new ConfigurationManager(args[0]);
} else {
		cm = new ConfigurationManager(SpeechRecogBot.class.getResource("SpeechRecogBot.config.xml"));
		}

	Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
	recognizer.allocate();
	listening.setText("Listening");
// start the microphone or exit if not
		        Microphone microphone = (Microphone) cm.lookup("microphone");
		        if (!microphone.startRecording()) {
		            JOptionPane.showMessageDialog(null,"Cannot start microphone.","Error",JOptionPane.ERROR_MESSAGE);
		            recognizer.deallocate();
		            System.exit(1);
		        }

		        // loop the recognition until the program exits.
		        while (true) {
		        	listening.setVisible(true);
		            Result result = recognizer.recognize();
		            
		        if (result != null) {
		                resultText = result.getBestFinalResultNoFiller();
		                userSpeechDisp.setText("You said: " + '\n' + resultText );
		               
		                
//---------------------------------| Basic Introduction Part |-------------------------------------//            
		//Greetings
		if((resultText.equalsIgnoreCase("hey computer")) ||
		  (resultText.equalsIgnoreCase("hi computer")))
		        {
		        try{
		        	
		        	String[] greet = {
							  "Hello there!",
							  "Yes, Milord? ",
							  "What can I do for you?",
							  "What can I help you with?",
							  "What is it?"};
					String greet_ran = greet[ran.nextInt(greet.length)];
					slnDisp.setText(greet_ran);
					answerToStr = slnDisp.getText();
					textToSpeech=("Say "+"\""+answerToStr+"\"");
					Runtime.getRuntime().exec(textToSpeech);
					TimeUnit.SECONDS.sleep(4);
					microphone.clear();					
		          } catch(Exception er) {} }
		                		 		                
		                
		// Name              
					if(resultText.equalsIgnoreCase("do you have a name"))
				        {
				        try{
				        slnDisp.setText("I have no name but you can call me computer") ;
				        answerToStr = slnDisp.getText();
						textToSpeech=("Say "+"\""+answerToStr+"\"");
						Runtime.getRuntime().exec(textToSpeech);
						TimeUnit.SECONDS.sleep(4);
						microphone.clear();
				          } catch(Exception er) {} }
	
		 
		
			
			//Being
			if(resultText.equalsIgnoreCase("how are you") ||
					   (resultText.equalsIgnoreCase("what's up"))) 
					{
					try{
					slnDisp.setText("I'm good, thanks for asking") ;
					answerToStr = slnDisp.getText();
					textToSpeech=("Say "+"\""+answerToStr+"\"");
					Runtime.getRuntime().exec(textToSpeech);
					TimeUnit.SECONDS.sleep(6);
					microphone.clear();   
					 } catch(Exception er) {} }
			
			//Age               
			if(resultText.equalsIgnoreCase("how old are you")||
			(resultText.equalsIgnoreCase("what is your age")))
			{
			try{
			slnDisp.setText("No idea... first thing I saw was a baby Dinosaur ") ;
			answerToStr = slnDisp.getText();
			textToSpeech=("Say "+"\""+answerToStr+"\"");
			Runtime.getRuntime().exec(textToSpeech);
			TimeUnit.SECONDS.sleep(8);
			microphone.clear();
			        			   
			    } catch(Exception er) {} }
			
//--------------------------------------| Humor Puns and Jokes |-------------------------------------//            			 
			//Coffee
			if(resultText.equalsIgnoreCase("make me a coffee"))
					                {
				try{
					slnDisp.setText("WHAT? MAKE IT YOURSELF!") ;
					answerToStr = slnDisp.getText();
					textToSpeech=("Say "+"\""+answerToStr+"\"");
					Runtime.getRuntime().exec(textToSpeech);
					TimeUnit.SECONDS.sleep(4);
					microphone.clear();
					
				    } catch(Exception er) {}}
			else if
			(resultText.equalsIgnoreCase("sue dough make me a coffee"))
            {
            	try{
            		userSpeechDisp.setText("You said: SUDO make me a coffee ");
            		slnDisp.setText("OK Master") ;
            		answerToStr = slnDisp.getText();
					textToSpeech=("Say "+"\""+answerToStr+"\"");
					Runtime.getRuntime().exec(textToSpeech);
					TimeUnit.SECONDS.sleep(4);
					microphone.clear();
            	} catch(Exception er) {}}
			
			//What does the fox say?
			if(resultText.equalsIgnoreCase("what does the fox say"))
            {
				try{
					String[] fox = {
							  "ding-ding-ding-dingeringeding",
							  "Hatee-hatee-hatee-ho! ",
							  "Wa-pa-pa-pa-pa-pa-pow!?",
							  "Jacha-chacha-chacha-chow!"};
					String foxHumor = fox[ran.nextInt(fox.length)];
					slnDisp.setText(foxHumor);
					answerToStr = slnDisp.getText();
					textToSpeech=("Say "+"\""+answerToStr+"\"");
					Runtime.getRuntime().exec(textToSpeech);
					TimeUnit.SECONDS.sleep(4);
					microphone.clear();
				} catch(Exception er) {}}

			//Jokes
			if(resultText.equalsIgnoreCase("tell me a joke")||
			  (resultText.equalsIgnoreCase("tell me another one")))			  
			
            {
				
				try{
					String[] jokes = {"Did you hear about the Italian chef that died?\nHe pasta way :(",
							  "So you need a defination of hardware? Well its the parts of a computer system that can be kicked. ",
							  "What do you call stormtroopers in jousting tournament?\nGame of Clones",
							  "Don't mess with me ! I know Karate, kung-fu, Judo, Jujitsu and 28 other dangerous words ",
							  "Jessie: Knock Knock!...\nI'm the one who knocks - Hisenberg",
							  "Why does Humpty Dumpty love autumn?\nBecause Humpty Dumpty had a great fall.",
							  "What was John Snows bakery called?\nYou know MUFFIN John Snow",
							  "Knock Knock!\nWhose there?\nMerry...\nMerry Who?\n Merry Christmas!"};
					String jokes_ran = jokes[ran.nextInt(jokes.length)];
					slnDisp.setFont(new Font("Cambria" , Font.BOLD, 17));
	            	slnDisp.setText(jokes_ran) ;
	            	answerToStr = slnDisp.getText();
					textToSpeech=("Say "+"\""+answerToStr+"\"");
					Runtime.getRuntime().exec(textToSpeech);
					TimeUnit.SECONDS.sleep(4);
					microphone.clear();
					} catch(Exception er) {}}
			
			//Nice Once
			if(resultText.equalsIgnoreCase("nice one"))
	        {
	        try{
	        slnDisp.setText("Just saying\"NICE ONE\" in an accent wont make you British!!! :/ ") ;
	        answerToStr = slnDisp.getText();
			textToSpeech=("Say "+"\""+answerToStr+"\"");
			Runtime.getRuntime().exec(textToSpeech);
			TimeUnit.SECONDS.sleep(4);
			microphone.clear();
	          } catch(Exception er) {} }

			
			
			
//---------------------------------| Time and System Apps |-------------------------------------//            
		 
			// Time 
		 if(resultText.equalsIgnoreCase("what time is it") || 
			resultText.equalsIgnoreCase("what is the current time")) 
			                {
		try{
			SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
			String formattedDate = time.format(new Date()).toString();
			slnDisp.setText("It is " + formattedDate ) ;
			answerToStr = slnDisp.getText();
			textToSpeech=("Say "+"\""+answerToStr+"\"");
			Runtime.getRuntime().exec(textToSpeech);
			TimeUnit.SECONDS.sleep(4);
			microphone.clear();
		    } catch(Exception er) {}
		        			}
		 //System Preferences
		 if(resultText.equalsIgnoreCase("open system preferences"))
			{
				try{
					slnDisp.setText("Opening System Preferences" ) ;
					pro =new ProcessBuilder("/usr/bin/open", "-a", "System Preferences").start();;
					answerToStr = slnDisp.getText();
					textToSpeech=("Say "+"\""+answerToStr+"\"");
					Runtime.getRuntime().exec(textToSpeech);
					TimeUnit.SECONDS.sleep(4);
					microphone.clear();
				} catch(Exception er) {}
			}   
		
		 if (resultText.equalsIgnoreCase("close system preferences"))
		    {
		        try{
		        slnDisp.setText("Closing System Preferences" ) ;
		        pro =new ProcessBuilder("/usr/bin/killall", " -kill-a", "System Preferences").start();
		        answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		        }catch(Exception ae){}
		    }
		 
		 
		 //Dictionary
		 if(resultText.equalsIgnoreCase("open dictionary"))
			{
				try{
					
					slnDisp.setText("Opening Dictionary" ) ;
					pro= Runtime.getRuntime().exec("/usr/bin/open -a Dictionary.app");;
					answerToStr = slnDisp.getText();
					textToSpeech=("Say "+"\""+answerToStr+"\"");
					Runtime.getRuntime().exec(textToSpeech);
					TimeUnit.SECONDS.sleep(4);
					microphone.clear();
				} catch(Exception er) {}
			}   
		
		 if (resultText.equalsIgnoreCase("close dictionary"))
		    {
		        try{	
		        slnDisp.setText("Closing Dictionary" ) ;
		        pro=Runtime.getRuntime().exec("/usr/bin/killall -kill Dictionary");
		        answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		        }catch(Exception ae){}
		    }
		 
		 
		 //Calculator
		 if(resultText.equalsIgnoreCase("open calculator"))
			{
				try{
					slnDisp.setText("Opening Calculator" ) ;
					pro= Runtime.getRuntime().exec("/usr/bin/open -a Calculator.app");;
					answerToStr = slnDisp.getText();
					textToSpeech=("Say "+"\""+answerToStr+"\"");
					Runtime.getRuntime().exec(textToSpeech);
					TimeUnit.SECONDS.sleep(4);	
					microphone.clear();
				} catch(Exception er) {}
			}   
		
		 else if (resultText.equalsIgnoreCase("close calculator"))
		    {
		        try{
		        slnDisp.setText("Closing Calculator" ) ;
		        pro=Runtime.getRuntime().exec("/usr/bin/killall -kill Calculator");
		        answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		        }catch(Exception ae){}
		    }
		   
			 
		                
		// Terminal
				 if(resultText.equalsIgnoreCase("open terminal"))
					{
					 	try{
							pro=Runtime.getRuntime().exec("/usr/bin/open -a terminal.app");
							slnDisp.setText("Opening Terminal" ) ;
							answerToStr = slnDisp.getText();
							textToSpeech=("Say "+"\""+answerToStr+"\"");
							Runtime.getRuntime().exec(textToSpeech);
							TimeUnit.SECONDS.sleep(4);
							microphone.clear();
					 	} catch(Exception er) {}
					}              
		
				 else if (resultText.equalsIgnoreCase("close terminal"))
				    {
				        try{
				        slnDisp.setText("Closing Terminal" ) ;
				        pro=Runtime.getRuntime().exec("/usr/bin/killall -kill Terminal");
				        answerToStr = slnDisp.getText();
						textToSpeech=("Say "+"\""+answerToStr+"\"");
						Runtime.getRuntime().exec(textToSpeech);
						TimeUnit.SECONDS.sleep(4);
						microphone.clear();
				        }catch(Exception ae){}
				    }
				 
				 //Camera
				 if (resultText.equalsIgnoreCase("open camera"))
				    {
				        try{

				        slnDisp.setText("Opening PhotoBooth" );
				        pro =new ProcessBuilder("/usr/bin/open", "-a", "Photo Booth.app").start();
				        answerToStr = slnDisp.getText();
						textToSpeech=("Say "+"\""+answerToStr+"\"");
						Runtime.getRuntime().exec(textToSpeech);
						TimeUnit.SECONDS.sleep(4);
						microphone.clear();
				        }catch(Exception ae){}
				    }
				 else if (resultText.equalsIgnoreCase("close camera"))
				    {
				        try{
				        slnDisp.setText("Closing PhotoBooth" );
				        pro =new ProcessBuilder("/usr/bin/killall", " -kill-a", "Photo Booth").start();
				        answerToStr = slnDisp.getText();
						textToSpeech=("Say "+"\""+answerToStr+"\"");
						Runtime.getRuntime().exec(textToSpeech);
						TimeUnit.SECONDS.sleep(4);
						microphone.clear();
				        }catch(Exception ae){}
				    }
				
//---------------------------------| Websites and Apps |-------------------------------------//            
		                   					
	   // Google
	   if(resultText.equalsIgnoreCase("open goo gall")) 
        {
		   try{
			   userSpeechDisp.setText("You said: \n open Google");
			   slnDisp.setText("Opening: www.google.com ") ;
			   String url_open ="http://www.google.com";
			   java.awt.Desktop.getDesktop().browse(java.net.URI.create(url_open));
			   answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		   } catch(Exception er) {}
        }
	   
	   //YouTube
	   if(resultText.equalsIgnoreCase("open you tube")) 
       {
		   try{
			   slnDisp.setText("Opening: www.youtube.com ") ;
			   String url_open ="http://www.youtube.com";
			   java.awt.Desktop.getDesktop().browse(java.net.URI.create(url_open));
			   answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		   } catch(Exception er) {}
       }
	     
	   //Facebook
	   if(resultText.equalsIgnoreCase("open face book")) 
       {
		   try{
			   slnDisp.setText("Opening: www.facebook.com ") ;
			   String url_open ="http://www.facebook.com";
			   java.awt.Desktop.getDesktop().browse(java.net.URI.create(url_open));
			   answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		   } catch(Exception er) {}
       }
	 
	   //Facebook
	   if(resultText.equalsIgnoreCase("open tweet err")) 
       {
		   try{
			   slnDisp.setText("Opening: www.twitter.com ") ;
			   String url_open ="http://www.twitter.com";
			   java.awt.Desktop.getDesktop().browse(java.net.URI.create(url_open));
			   answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		   } catch(Exception er) {}
       }
         // iTunes
		 if(resultText.equalsIgnoreCase("open music"))
						 			{
						 				try{
						 					slnDisp.setText("Opening iTunes" ) ;
					 						pro= Runtime.getRuntime().exec("/usr/bin/open -a iTunes.app");;
					 						answerToStr = slnDisp.getText();
					 						textToSpeech=("Say "+"\""+answerToStr+"\"");
					 						Runtime.getRuntime().exec(textToSpeech);
					 						TimeUnit.SECONDS.sleep(4);
					 						microphone.clear();
						 				} catch(Exception er) {}
						 			}
		 else if (resultText.equalsIgnoreCase("close music"))
		    {
		        try{
		        slnDisp.setText("Closing iTunes" ) ;
		        pro=Runtime.getRuntime().exec("/usr/bin/killall -kill iTunes");
		        answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		        }catch(Exception ae){}
		    }
		 
		 
		 
		
		 //Google Chrome
		 if(resultText.equalsIgnoreCase("open chrome") ||
		(resultText.equalsIgnoreCase("open browser")))
			{
				try{
					slnDisp.setText("Opening chrome" ) ;
					pro =new ProcessBuilder("/usr/bin/open", "-a", "Google Chrome").start();
					answerToStr = slnDisp.getText();
					textToSpeech=("Say "+"\""+answerToStr+"\"");
					Runtime.getRuntime().exec(textToSpeech);
					TimeUnit.SECONDS.sleep(4);	
					microphone.clear();
				} catch(Exception er) {}
			}    
		 else if (resultText.equalsIgnoreCase("close chrome") ||
			(resultText.equalsIgnoreCase("close browser")))
		    {
		        try{	
		        slnDisp.setText("Closing Chrome") ;
		        pro=Runtime.getRuntime().exec("/usr/bin/killall -kill Safari");
		        pro =new ProcessBuilder("/usr/bin/killall", " -kill-a", "Google Chrome").start();
		        answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		        }catch(Exception ae){}
		    }
		 
		 //VLC
		 if(resultText.equalsIgnoreCase("open v l c"))
			{
				try{
					slnDisp.setText("Opening VLC Media Player" ) ;
					pro= Runtime.getRuntime().exec("/usr/bin/open -a VLC.app");;
					answerToStr = slnDisp.getText();
					textToSpeech=("Say "+"\""+answerToStr+"\"");
					Runtime.getRuntime().exec(textToSpeech);
					TimeUnit.SECONDS.sleep(4);
					microphone.clear();
				} catch(Exception er) {}
			}   
		
		 else if (resultText.equalsIgnoreCase("close v l c"))
		    {
		        try{
		        slnDisp.setText("Closing VLC Media Player" ) ;
		        pro=Runtime.getRuntime().exec("/usr/bin/killall -kill VLC");
		        answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		        }catch(Exception ae){}
		    }
		 
		 
		 //Photoshop
		 if (resultText.equalsIgnoreCase("open photo shop"))
		    {
		        try{
		        slnDisp.setText("Opening Adobe Photoshop");
		        pro =new ProcessBuilder("/usr/bin/open", "-a", "Adobe Photoshop CS6").start();
		        answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		        }catch(Exception ae){}
		    }
		 else if (resultText.equalsIgnoreCase("close photo shop"))
				    {
				        try{
				        slnDisp.setText("Closing Adobe Photoshop") ;
				        pro =new ProcessBuilder("/usr/bin/killall", " -kill-a", "Adobe Photoshop CS6").start();
				        answerToStr = slnDisp.getText();
						textToSpeech=("Say "+"\""+answerToStr+"\"");
						Runtime.getRuntime().exec(textToSpeech);
						TimeUnit.SECONDS.sleep(4);
						microphone.clear();
				        }catch(Exception ae){}}
		  		if (resultText.equalsIgnoreCase("stop recognition") ||(resultText.equalsIgnoreCase("good bye computer")))
		    {
		        try{
		        slnDisp.setText("Good Bye! See you again") ;
		        answerToStr = slnDisp.getText();
				textToSpeech=("Say "+"\""+answerToStr+"\"");
				Runtime.getRuntime().exec(textToSpeech);
				TimeUnit.SECONDS.sleep(4);
				microphone.clear();
		        System.exit(0);
		        }catch(Exception ae){}}
		            } else {
		            	slnDisp.setText("I can't hear what you said.\n");
		        
		            }
		        }
		    			}

		}
			
