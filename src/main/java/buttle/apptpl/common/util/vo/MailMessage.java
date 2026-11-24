package buttle.apptpl.common.util.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.vo.CommonAtchFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailMessage implements Serializable {
    private static final long serialVersionUID = -1591845862561513301L;

    private Boolean html;
    private List<String> to;
    private String subject;
    private String message;
    private String attachmentName;
    private List<CommonAtchFile> multipartFile;

    public boolean getHtml() {
        if (html == null) {
            html = false;
        }
        return html;
    }

    public void addTo(String emlAddr) {
        if (to == null) {
            to = new ArrayList<>();
        }

        to.add(emlAddr);
    }

    public void clearTo() {
        if (to == null) {
            to = new ArrayList<>();
        }
        to.clear();
    }

    @Deprecated
    public void setTo(String emlAddr) {
        if (to == null) {
            to = new ArrayList<>();
        }
        to.clear();
        to.add(emlAddr);
    }
}
