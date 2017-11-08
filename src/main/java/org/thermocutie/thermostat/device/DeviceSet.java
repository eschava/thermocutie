package org.thermocutie.thermostat.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thermocutie.thermostat.xml.IXmlFilePersistableHelper;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Registry of {@link IDevice}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class DeviceSet implements IXmlFilePersistableHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(DeviceSet.class);

    private List<IDevice> deviceList = new ArrayList<>();
    private File file;

    @Override
    public String getRootTag() {
        return "Devices";
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public File getFile() {
        return file;
    }

    public synchronized void loadFromXml(Element element) {
        childElements(element).
                map(this::loadDeviceFromXml)
                .filter(Objects::nonNull)
                .forEach(this::addDevice);
    }

    private IDevice loadDeviceFromXml(Element element) {
        try {
            String tagName = element.getTagName();
            String deviceClassName = IDevice.class.getPackage().getName() + "." + tagName;
            Class<?> deviceClass = Class.forName(deviceClassName);
            if (!IDevice.class.isAssignableFrom(deviceClass)) {
                LOGGER.error("Class " + deviceClassName + " is not device class");
                return null;
            }

            IDevice device = (IDevice) deviceClass.newInstance();
            device.loadFromXml(element);
            return device;
        } catch (Throwable e) {
            LOGGER.error("Cannot load device from XML " + element, e);
            return null;
        }
    }

    public synchronized void saveToXml(Element element) {
        // TODO: invent something better
        while (element.getFirstChild() != null)
            element.removeChild(element.getFirstChild());

        deviceList.forEach(device -> {
            String tagName = device.getClass().getSimpleName();
            Element deviceElement = element.getOwnerDocument().createElement(tagName);
            device.saveToXml(deviceElement);
            element.appendChild(deviceElement);
        });
    }

    public synchronized void addDevice(IDevice device) {
        deviceList.add(device);
    }

    public synchronized <T> Collection<T> getDevices(Class<T> deviceClass) {
        //noinspection unchecked
        return deviceList.stream()
                .filter(device -> deviceClass.isAssignableFrom(device.getClass()))
                .map(device -> (T)device)
                .collect(Collectors.toList());
    }
}
