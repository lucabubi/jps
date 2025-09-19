package it.polito.wa2.g19.crm.entities

import jakarta.persistence.*
import java.time.LocalDateTime

enum class Channel {
    PHONE_CALL,
    TEXT_MESSAGE,
    EMAIL
}

enum class State {
    RECEIVED,
    READ,
    DISCARDED,
    PROCESSING,
    DONE,
    FAILED
}

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

@Entity
class Message(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var sender: String,
    var date: LocalDateTime,
    var subject: String?,
    var body: String?,
    var channel: Channel,
    private var state: State = State.RECEIVED,
    var priority: Priority,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    var history: MutableList<History> = mutableListOf()
) {
    fun getState(): State {
        return state
    }

    fun setState(newState: State) {
        state = newState
    }
}

@Entity
class History(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var date: LocalDateTime,
    var state: State,
    var comment: String?,
    @ManyToOne
    var message: Message
) {

}
