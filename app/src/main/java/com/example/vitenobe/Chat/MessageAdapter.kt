package com.example.vitenobe.Chat

import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vitenobe.R

class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    // Criando o ViewHolder para o item da mensagem
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    // Ligando os dados da mensagem ao item da lista
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    // ViewHolder para as mensagens
    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageTextView: TextView = view.findViewById(R.id.messageText)

        // Método para associar dados à View
        fun bind(message: Message) {
            messageTextView.text = message.text
            // Aqui, podemos adicionar lógica para formatar ou exibir a mensagem dependendo do senderId, etc.

            // Exemplo: Alterando a posição da mensagem se for do remetente ou destinatário
            if (message.senderId == "userId") {
                messageTextView.gravity = Gravity.END  // A mensagem do usuário pode aparecer do lado direito
                // TO DO
            // messageTextView.setBackgroundResource(R.drawable.message_background_sent)
            } else {
                messageTextView.gravity = Gravity.START // Mensagens recebidas apareceriam no lado esquerdo
                // TO DO
            //messageTextView.setBackgroundResource(R.drawable.message_background_received)
            }
        }
    }
}

