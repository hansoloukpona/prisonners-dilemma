package fr.uga.l3miage.pc.prisonersdilemma.userside.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

//User-Side

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimpleExchangeDTO {
    private String from;
    private String to;
    private String content;

}
