# AlmaNotes Mobile

AlmaNotes Mobile è l'app Android per AlmaNotes, un'applicazione pensata per facilitare la condivisione, la ricerca e la consultazione di appunti universitari. 

L'app permette agli utenti di cercare materiale, scaricare documenti in formato PDF, recensire gli appunti e gestire il proprio profilo, con l'aggiunta di un sistema di notifiche e badge (gamification).

## Funzionalità Principali

* **Autenticazione**: Login e registrazione utente.
* **Gestione Appunti**: Ricerca degli appunti, visualizzazione dei dettagli e sistema di recensioni.
* **Gestione File**: Download dei PDF con visualizzatore integrato (`PdfViewer`). Tracciamento dei file caricati e scaricati.
* **Gamification e Notifiche**: Sistema di badge per gli utenti e notifiche push integrate.
* **Profilo Utente**: Gestione dei propri dati e personalizzazione dell'app.
* **Supporto Temi**: Modalità chiara e scura.

## Struttura del Progetto

Il codice sorgente principale si trova sotto `app/src/main/java/com/example/almanotesmobile/`:

* `data/`: Contiene i modelli dati (`Note`, `Theme`, `AppNotification`), i DAO di Room e i Repository (`AuthRepository`, `NoteRepository`, ecc.).
* `ui/`: Contiene tutta la parte visiva dell'app.
  * `composables/`: Componenti UI riutilizzabili (Header, Footer, ecc.).
  * `screens/`: Le singole schermate dell'applicazione (Home, Login, Profile, PdfViewer, ecc.).
  * `navigation/`: Gestione del routing interno tramite Jetpack Navigation.
  * `theme/`: Definizione di colori, tipografia e temi di Compose.
  * `viewmodel/`: I ViewModel associati alle varie schermate per la gestione dello stato e della logica di business.
* `utils/`: Classi di utilità generiche (es. `PdfDownloader`).
