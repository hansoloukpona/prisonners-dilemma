package fr.uga.l3miage.pc.prisonersdilemma.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

//User-Side

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class SimpleInformationExchange {
    private String from;
    private String to;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
