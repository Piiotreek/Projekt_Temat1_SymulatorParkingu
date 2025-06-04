
# ParkingSimulator - Program symulacji parkingu.
 
Program ten symuluje zarządzanie parkingiem z następującymi funkcjami:
  - Rejestracja wjazdu i wyjazdu pojazdów z numerem rejestracyjnym i znacznikami czasowymi.
  - Obsługuje wiele typów pojazdów: Samochód i Van dostawczy.
  - Automatycznie oblicza opłaty na podstawie czasu parkowania i typu pojazdu.
  - Śledzi dostępne miejsca parkingowe.
  - Utrzymuje listę aktualnie zaparkowanych pojazdów.
  - Zapewnia historię parkowania, raporty dzienne i symulację czasu.
 
  Program jest zaprojektowany jako pojedynczy plik Java z interfejsem konsolowym.
 
 # Użycie:
  Uruchom program i postępuj zgodnie z instrukcjami na ekranie, aby interagować z parkingiem.

# Wymagania
- Zainstalowany Docker

# Autorzy: 
Piotr Poletyło i Aleksander Ostrowski

# Data: 
2024-06

# Jak uruchomić za pomocą Docker
```bash
git clone https://github.com/Piiotreek/Projekt_Temat1_SymulatorParkingu.git
cd Projekt_Temat1_SymulatorParkingu
docker build -t symulator-parkingu .
docker run symulator-parkingu
