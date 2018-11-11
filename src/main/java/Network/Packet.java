package Network;

import Config.Status;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * Created by son on 2018-10-28.
 */

public class Packet {
    @Getter @Setter String protocol;
    @Getter @Setter String localAddress;
    @Getter @Setter String remoteAddress;
    @Getter @Setter String flag;
    @Getter @Setter String pid;
    @Getter @Setter String processName;

    public Packet(final String protocol, final String localAddress, final String remoteAddress, final String flag) {
        this.protocol = protocol;
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
        this.flag = flag;
    }

    boolean isProcessName() {
        return !StringUtils.isEmpty(processName);
    }

    private String getStatusMessage(final String status) {
        return Status.getMessage(status);
    }
}
