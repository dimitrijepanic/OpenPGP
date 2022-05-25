package etf.openpgp.pd180205dtj180023d;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		List<PGPProtocol.PGPOptions> list=new ArrayList<>();
		list.add(PGPProtocol.PGPOptions.COMPATIBILITY);
		PGPProtocol.encrypt("D:\\GIT_projekti\\ZPproject\\jana.txt",PGPEncryptor.SymetricKeyAlgorithm.TRIPLEDES, list);
		AppMainFrame appMainFrame = new AppMainFrame();

	}

}
