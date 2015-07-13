/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

import fr.insalyon.creatis.grida.client.GRIDAClient;
import fr.insalyon.creatis.grida.client.GRIDAClientException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nouha Boujelben
 * To test this file you have to make a SSH TUNNEL FOR GRIDA 
    * ssh -L [LOCALPORT]:localhost:[GRIDA PORT] [USER NAME]@vip.creatis.insa-lyon.fr -f -N
    in this test: 
    *the grida port is 9011 (you can change it ) 
    *the grida host "kingkong.grid.creatis.insa-lyon.fr"
    *the proxy point to a file in "/home/boujelben/.vip/proxies/biomed/x509up_server"
 */
public class LFCUtilsTest {

    static GRIDAClient gr;

    public LFCUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("logfile.name", "./ssha.log");
        gr = new GRIDAClient("kingkong.grid.creatis.insa-lyon.fr", 9011, "/home/boujelben/.vip/proxies/biomed/x509up_server");
    }

   
    @Test
    public void testGrida() {
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(LFCUtilsTest.class);
        try {
            gr.createFolder("/grid/biomed/creatis/vip/data/users/nouha_boujelben", "t");

        } catch (GRIDAClientException ex) {
            logger.error(ex.getMessage());

        }

    }

}
