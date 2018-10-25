package Model.Watcher;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.json.simple.JSONObject;

import Model.EtudiantExamenInfo;

public class UsbWatcher extends Watcher {

	static String TYPE = "USB";

	public UsbWatcher() {
		super(TYPE);
	}

	protected JSONObject createDataBeforeSendEvent(EtudiantExamenInfo etudiantExamenInfo) {
		JSONObject datas = new JSONObject();
		datas.put("prenom", etudiantExamenInfo.getPrenomEtudiant());
		return datas;
	}
	
	@Override
	public void run() {
		// Ligne de commande qui renvoie le nombre de clés usb de type mass branchées 
		String cmd = "lsusb -t | grep Class=Mass | wc -l";
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
		
		int n_usb = -1;
		while (true) {
			try {
				Process usb = pb.start();
				
				usb.waitFor();
				BufferedReader reader = new BufferedReader(new InputStreamReader(usb.getInputStream()));
				StringBuilder builder = new StringBuilder();
				String line = null;
				while ( (line = reader.readLine()) != null) {
				   builder.append(line);
				   
				}
				int result = Integer.parseInt(builder.toString());
				
				if (n_usb == -1){
					n_usb = result;
					
				} else {
					if (n_usb < result){
						System.out.println("Connexion d'une clé USB");
						
					} else if (n_usb > result) {
						System.out.println("Deconnexion d'une clé USB");
						
					}
					n_usb = result;
				}
				Thread.sleep(500);
			} catch (IOException | InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
	}

}