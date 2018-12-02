import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Classe qui correspond à la connexion à un client
 * Chaque client va communiquer avec le serveur par un port précis qui lui est attribué
 * @author etudiant
 * 
 */
public class ClientHandler extends Thread{

	private String nomClient;
	private String IDexam;
	private int port;
	private FileOutputStream sortieVideo;
	private DatagramSocket socketSpecialClient;
	private String chemin;

	public ClientHandler(String NC, String M, int P) throws FileNotFoundException
	{
		nomClient = NC;
		IDexam = M;
		port = P;
		chemin = idVersChemin();
		
		sortieVideo = new FileOutputStream(chemin + nomClient + ".surv",true);
	}

	public void run()
	{
		// Socket du serveur
		try {
			System.out.println( "Le serveur écoute le client " + nomClient + " sur le port " + port + "." ) ;

			socketSpecialClient = new DatagramSocket( port ) ;
			byte[] receptionVideo = new byte[2048];

			while(true){                 				
				DatagramPacket paquetVideo = new DatagramPacket(receptionVideo, receptionVideo.length);

				socketSpecialClient.receive(paquetVideo);

				//System.out.println("DEBUG: Client: " + nomClient + ", Port ecoute: "+ port + ", Port reception: " + paquetVideo.getPort());
				ajoutElementVideo(paquetVideo);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * Ajoute le contenu du paquet à la suite de la vidéo du client
	 * @param paquet
	 */
	public void ajoutElementVideo(DatagramPacket paquet){
		try
		{
			//On l'écrit dans la flux
			sortieVideo.write(paquet.getData());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{}
	}

	/**
	 * Ferme le socket serveur
	 */
	public void stopSocket(){
		System.out.println("Fermeture du client " + nomClient);
		socketSpecialClient.close();
	}
	
	public String idVersChemin()
	{
		chemin = IDexam.toString();


		// On ajoute les 0 pour avoir une haine de 10 caractères
		while (chemin.length() != 10)
		{
			chemin = "0" + chemin;
		}

		/* On transforme la chaine en tableau de char pour pouvoir intercaler
           des / entre chacun des caractères de la chaîne */
		char[] tmp = chemin.toCharArray();
		chemin = "";

		for(int i = 0; i < 10; i++)
		{
			chemin = chemin + "/" + tmp[i];
		}

		chemin = "/opt/data_dir" + chemin + "/" + nomClient + "/";

		return chemin;
	}
}