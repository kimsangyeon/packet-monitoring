package Controller;

import Network.PacketContainer;
import Network.model.AjaxModel;
import Network.model.Packet;
import Service.NetworkService;
import Service.WindowSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.*;

/**
 * Created by son on 2018-10-28.
 */

@Slf4j
@RestController
@RequestMapping(value = "/network")
public class NetworkController {
    private NetworkService networkService;
    private WindowSystemService windowSystemService;

    @Autowired
    public NetworkController(final NetworkService networkService, final WindowSystemService windowSystemService) {
        this.networkService = networkService;
        this.windowSystemService = windowSystemService;
    }

    @RequestMapping()
    public ModelAndView analyze() throws IOException {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("systemInfos", windowSystemService.getSystemOsName());
        return modelAndView;
    }

    @RequestMapping("/detect")
    public AjaxModel detect(
            @RequestParam(value = "isProcName") final boolean isProcName
    ) throws IOException {
        PacketContainer packetContainer = networkService.analyze();

        if (!StringUtils.isEmpty(isProcName) && isProcName) {
            Map<Integer, List<String>> ports = new HashMap<>();
            for (Packet packet : packetContainer.getPackets()) {
                if (!ports.containsKey(packet.getLocalPort())) {
                    List<String> procInfo = new ArrayList<>();
                    String pid = windowSystemService.getProcessIdFromPort(packet.getLocalPort());
                    String procName = windowSystemService.getProcessNameFromPid(pid);

                    procInfo.add(pid);
                    procInfo.add(procName);
                    ports.put(packet.getLocalPort(), procInfo);
                }

                List<String> proc = ports.get(packet.getLocalPort());
                packet.setProcessName(proc.get(1));
            }
        }

        return new AjaxModel(
                true,
                "detect",
                packetContainer.getPackets(),
                packetContainer.getPackets().size(),
                null
        );
    }

    @RequestMapping("/viewDevices")
    public ModelAndView viewDevices() {
        ModelAndView modelAndView = new ModelAndView("devices");
        modelAndView.addObject("devices", networkService.getNetworkDevices());
        return modelAndView;
    }

    @RequestMapping("/selectDevice")
    public boolean selectDevice(@RequestParam(value = "id") final String id) {
        return networkService.activityDevice(id);
    }

    @RequestMapping("/checkDevice")
    public AjaxModel checkDevice() {
        boolean result = networkService.checkDevice();
        return new AjaxModel(true, "success", result, null);
    }

    @RequestMapping(value = "/processName", method = RequestMethod.POST)
    public AjaxModel processNameFormPort(@RequestParam(value = "port") final int port) {
        String processName = "";
        try {
            String pid = windowSystemService.getProcessIdFromPort(port);
            processName = windowSystemService.getProcessNameFromPid(pid);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }

        return new AjaxModel(true, "processName", processName);
    }

    @RequestMapping("/cpuAmount")
    public AjaxModel cpuAmout() {
        return new AjaxModel(true, "cpuAmount", windowSystemService.getCpuAmount());
    }
}