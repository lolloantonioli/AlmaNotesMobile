package com.example.almanotesmobile.data

import com.example.almanotesmobile.data.local.Note

object MockData {
    private val now = System.currentTimeMillis()

    val notes = listOf(
        Note(title = "Analisi Matematica 1",       professorName = "Prof. Cinti",      subject = "Ing. e Scienze Informatiche", downloadCount = 2_400, rating = 4.8f, uploaderName = "marco_bianchi",  uploadedAt = now - 7 * 86_400_000L),
        Note(title = "Fisica 1",                   professorName = "Prof. Morandi",    subject = "Ing. e Scienze Informatiche", downloadCount = 1_870, rating = 4.5f, uploaderName = "sara_rossi",      uploadedAt = now - 14 * 86_400_000L),
        Note(title = "Algoritmi e Strutture Dati", professorName = "Prof. Martini",    subject = "Ing. e Scienze Informatiche", downloadCount = 1_540, rating = 4.9f, uploaderName = "luca_ferrari",    uploadedAt = now - 5 * 86_400_000L),
        Note(title = "Algebra Lineare",            professorName = "Prof. Fioresi",    subject = "Ing. e Scienze Informatiche", downloadCount =   890, rating = 4.3f, uploaderName = "giulia_conti",    uploadedAt = now - 2 * 86_400_000L),
        Note(title = "Programmazione",             professorName = "Prof. Montesi",    subject = "Ing. e Scienze Informatiche", downloadCount =   760, rating = 4.7f, uploaderName = "andrea_neri",     uploadedAt = now - 3_600_000L),        // 1h fa
        Note(title = "Basi di Dati",               professorName = "Prof. Golfarelli", subject = "Ing. e Scienze Informatiche", downloadCount =   430, rating = 4.6f, uploaderName = "chiara_russo",    uploadedAt = now - 3 * 86_400_000L),
        Note(title = "Reti di Calcolatori",        professorName = "Prof. Corradi",    subject = "Ing. e Scienze Informatiche", downloadCount =   320, rating = 4.4f, uploaderName = "filippo_guerra",  uploadedAt = now - 60_000L),            // 1min fa
        Note(title = "Sistemi Operativi",          professorName = "Prof. Ghini",      subject = "Ing. e Scienze Informatiche", downloadCount =   280, rating = 4.2f, uploaderName = "elena_mori",      uploadedAt = now - 30 * 60_000L),       // 30min fa
    )
}