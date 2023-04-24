# Tv-Serier-Kandidatoppgave

Valgene jeg har tatt

Denne oppgaven har jeg løst ved å bruke intellijs Maven rammeverk. Fordelen er at byggeprossessen er enkel og det er enkelt å implenmentere avhengighetene. Programmet leser inn en txt fil som ligger i "Oppgave" mappen. Det gjøres et API kall på hver linje som leses, og resultatet parses og lager et Serie objekt med informasjon om serien. Hver Serie lagres i en ArrayList. 

Jeg ser at oppgaven har bedt om at informasjonen skal legges i en lokal database, og kan skjønne at det gjør sortering og uthenting av rapporter enklere. Jeg har ikke så mye erfaring med integrering av databaser med javaprogrammer, så for meg ble det enklere å legge objektene i en liste. Jeg har koblet programmet mot en SQLite database, men det henter ikke ut data derfra.

For å unngå HTTP 429 error har jeg lagt til en forsinkelse mellom hvert api kall på et halvt sekund. Dette er den raskeste måten jeg fikk til å gjøre alle api-kallene og det fungerer bra.

For å finne TV-programmet for neste uke sjekker programmet hva dagens dato er, og bruker det til å finne ut hvilken dato hver ukedag neste uke er og slå opp om programmet går på en og en dato. Jeg gjorde det på denne måten slik at programmet blir dynamisk og endrer seg fra uke til uke.

Brukeren velger rapporter som hentes ut ved bruk av input. Alle rapportene kan opprettes og velges bortsett fra "Recommended show" hvor jeg ikke skjønte hva slags anbefalinger som skulle gis.
