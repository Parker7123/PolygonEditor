Projekt pisany w wersji JAVA 11

Przyciski
Esc - anuluje rysowanie

Reszta funkcjonalności dostępna za pomocą krycisków w UI i myszki.

Algorytm relacji:
- znajduje się w metodzie RelationManager.applyRelationsStartingAtVertex
1. Po poruszeniu wierzchołkiem:
2. jeśli krawędź po prawej stronie wierzchołka ma relację długości lub 
prostopadłości zaaplikuj relację (z flagą backwards = false) i przejdź 
do kolejnej krawędzi po prawej stronie i wróć do punktu 2 jeśli napotkana 
krawędź nie ma żadnej relacji przerwij pętlę
4. analogicznie dla krawędzi po lewej stronie wierzchołka
(wywołaj metody aplikowania poszczególnych relacji z flagą backwards = true)

Aplikacja relacji w metodach:
- RelationManager.applyPerpendicularRelation
- RelationManager.applyLengthRelation
