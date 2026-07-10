package com.robotopia.androidstudiolite.feature.editor.model

import com.robotopia.androidstudiolite.feature.projects.model.ProjectId

data class DocumentId(
    val projectId: ProjectId,
    val relativePath: String,
)

data class OpenDocument(
    val id: DocumentId,
    val content: String,
    val isDirty: Boolean,
)
