package fr.uga.l3miage.pc.prisonersdilemma.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Message {
    private String from;
    private String to;
    private String content;
}
