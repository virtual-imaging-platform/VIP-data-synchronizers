/*
Copyright 2015

CREATIS
CNRS UMR 5220 -- INSERM U1044 -- Université Lyon 1 -- INSA Lyon

Authors

Nouha Boujelben (nouha.boujelben@creatis.insa-lyon.fr)
Tristan Glatard (tristan.glatard@creatis.insa-lyon.fr)

This software is a daemon for file synchronization between SFTP
servers and the LCG File Catalog (LFC).

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can use,
modify and/ or redistribute the software under the terms of the
CeCILL-B license as circulated by CEA, CNRS and INRIA at the following
URL "http://www.cecill.info".

As a counterpart to the access to the source code and rights to copy,
modify and redistribute granted by the license, users are provided
only with a limited warranty and the software's author, the holder of
the economic rights, and the successive licensors have only limited
liability.

In this respect, the user's attention is drawn to the risks associated
with loading, using, modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean that it is complicated to manipulate, and that also
therefore means that it is reserved for developers and experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards
their requirements in conditions enabling the security of their
systems and/or data to be ensured and, more generally, to use and
operate it in the same conditions as regards security.

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-B license and that you accept its terms.
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
