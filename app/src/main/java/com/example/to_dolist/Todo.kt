data class Todo(
    var title: CharSequence,
    var isChecked: Boolean = false,
    var isEditable: Boolean = false,
    var status: String = "Pending"
)
