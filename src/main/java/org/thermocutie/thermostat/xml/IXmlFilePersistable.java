package org.thermocutie.thermostat.xml;

import java.io.File;

/**
 * Interface for objects that are persistable to XML file
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public interface IXmlFilePersistable extends IXmlPersistable {
    void setFile(File file);

    void loadFromFile();

    void saveToFile();
}
