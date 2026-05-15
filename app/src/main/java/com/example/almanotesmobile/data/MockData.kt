package com.example.almanotesmobile.data

import com.example.almanotesmobile.data.local.Note

object MockData {
    private val now = System.currentTimeMillis()

    // PDF demo open-access - sostituire con URL del proprio backend in produzione
    private const val PDF_ALGORITMI  = "https://www.africau.edu/images/default/sample.pdf"
    private const val PDF_ANALISI    = "https://www.orimi.com/pdf-test.pdf"
    private const val PDF_FISICA     = "https://www.africau.edu/images/default/sample.pdf"
    private const val PDF_ALGEBRA    = "https://www.orimi.com/pdf-test.pdf"
    private const val PDF_PROG       = "https://www.africau.edu/images/default/sample.pdf"
    private const val PDF_BASI_DATI  = "https://www.orimi.com/pdf-test.pdf"
    private const val PDF_RETI       = "https://www.africau.edu/images/default/sample.pdf"
    private const val PDF_SO         = "https://www.orimi.com/pdf-test.pdf"

    val notes = listOf(
        Note(title = "Appunti Analisi Matematica 1",       courseName = "Analisi Matematica 1",       professorName = "Prof. Cinti",      subject = "Ingegneria e Scienze Informatiche", filePath = PDF_ANALISI,    downloadCount = 4, rating = 4.8f, ratingCount = 42, uploaderName = "marco_bianchi",  uploadedAt = now - 7 * 86_400_000L),
        Note(title = "Appunti Fisica 1",                   courseName = "Fisica 1",                   professorName = "Prof. Morandi",    subject = "Ingegneria e Scienze Informatiche", filePath = PDF_FISICA,     downloadCount = 7, rating = 4.5f, ratingCount = 31, uploaderName = "sara_rossi",      uploadedAt = now - 14 * 86_400_000L),
        Note(title = "Appunti Algoritmi e Strutture Dati", courseName = "Algoritmi e Strutture Dati", professorName = "Prof. Martini",    subject = "Ingegneria e Scienze Informatiche", filePath = PDF_ALGORITMI,  downloadCount = 5, rating = 4.9f, ratingCount = 58, uploaderName = "luca_ferrari",    uploadedAt = now - 5 * 86_400_000L),
        Note(title = "Appunti Algebra Lineare",            courseName = "Algebra Lineare",            professorName = "Prof. Fioresi",    subject = "Matematica",                        filePath = PDF_ALGEBRA,    downloadCount =   3, rating = 4.3f, ratingCount = 19, uploaderName = "giulia_conti",    uploadedAt = now - 2 * 86_400_000L),
        Note(title = "Appunti Programmazione",             courseName = "Programmazione",             professorName = "Prof. Montesi",    subject = "Ingegneria e Scienze Informatiche", filePath = PDF_PROG,       downloadCount =   2, rating = 4.7f, ratingCount = 27, uploaderName = "andrea_neri",     uploadedAt = now - 3_600_000L),
        Note(title = "Appunti Basi di Dati",               courseName = "Basi di Dati",               professorName = "Prof. Golfarelli", subject = "Ingegneria Informatica",            filePath = PDF_BASI_DATI,  downloadCount =   3, rating = 4.6f, ratingCount = 14, uploaderName = "chiara_russo",    uploadedAt = now - 3 * 86_400_000L),
        Note(title = "Appunti Reti di Calcolatori",        courseName = "Reti di Calcolatori",        professorName = "Prof. Corradi",    subject = "Ingegneria Informatica",            filePath = PDF_RETI,       downloadCount =   2, rating = 4.4f, ratingCount = 11, uploaderName = "filippo_guerra",  uploadedAt = now - 60_000L),
        Note(title = "Appunti Sistemi Operativi",          courseName = "Sistemi Operativi",          professorName = "Prof. Ghini",      subject = "Ingegneria e Scienze Informatiche", filePath = PDF_SO,         downloadCount =   1, rating = 4.2f, ratingCount =  8, uploaderName = "elena_mori",      uploadedAt = now - 30 * 60_000L),
    )
}