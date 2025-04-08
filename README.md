ViTeNoBE - App de Encontros Universitários
ViTeNoBE é uma aplicação móvel desenvolvida em Kotlin com Jetpack Compose, inspirada no conceito do Tinder, mas direcionada exclusivamente para estudantes da Universidade de Aveiro. O objetivo principal é facilitar o encontro e a socialização entre alunos com interesses em comum, através de uma interface moderna, intuitiva e segura.

✨ Funcionalidades principais:
Swipe entre perfis para mostrar interesse ou passar;

Sistema de matches: só podem trocar mensagens após interesse mútuo;

Chat em tempo real com troca de mensagens;

Ecrã de registo com informações personalizadas como curso, interesses e data de nascimento;

Autenticação via Firebase (email/password);

Gestão de utilizadores e mensagens com Firebase Realtime Database.

🧱 Estrutura do projeto:
Arquitetura MVVM com separação clara entre UI (Activities/Composables), lógica (ViewModels) e dados (Firebase);

Uso de Firebase Authentication e Realtime Database;

UI construída com Jetpack Compose para flexibilidade e modernidade;

RecyclerView e SwipeView para interações fluídas entre perfis;

Persistência de dados na nuvem com sincronização em tempo real;

Integração com Glide para carregamento dinâmico de imagens de perfil.

📱 Principais ecrãs:
Registration/Login

MainActivity (swipes)

MatchesActivity

ChatProfileActivity (mensagens por match)

SettingsActivity

💡 Público-Alvo:
Estudantes da Universidade de Aveiro interessados em conhecer novas pessoas dentro da sua comunidade académica.
