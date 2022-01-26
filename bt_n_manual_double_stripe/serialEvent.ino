void serialEvent() {
  clearCommand();
  sIt = 0;
  while (Serial.available()){
    c = Serial.read();
    command[sIt] = c;
    sIt++;
  }
  analizeString();
}

void btEvent(esp_spp_cb_event_t event, esp_spp_cb_param_t *param) {
  if(event == ESP_SPP_SRV_OPEN_EVT){
    Serial.println("Client connected. ->");
  }
  else if(event == ESP_SPP_CLOSE_EVT){
    Serial.println("Client disconnected. <-");
  }
}

void clearCommand() {
  for (int i=0; i<COMMAND_LENGTH; i++) {
    command[i] = '\0';
  }
}

void analizeString() {
  Serial.println(command);
  
  // Find the first delimited word.
  token = strtok(command, delimiter);
  tokens_counter = 0;
  // Find the following delimited words.
  while(token != NULL) {
    Serial.print("Token ");
    Serial.print(tokens_counter);
    Serial.print(": ");
    Serial.println(token);
    tokens_counter += 1;
    token = strtok(NULL, delimiter);
    if (token != NULL) {
      switch (token[0]) {
          case 'B': // Reference signal
                    brght = atoi(&command[1]);
                    brght = check(brght, 10, 100);
            break;
          case 'F': // Reference signal
                    freq = atoi(&command[1]);
                    freq = check(freq, 10, 10000);
            break;
          case 'N': // Reference signal
                    num = atoi(&command[1]);
                    num = check(num, 1, NUM_LEDS);
  
            break;
          case 'P': // Reference signal
            break;
          default:
                  Serial.print("ERROR: Command: '");
                  Serial.print(command);
                  Serial.println("' is not valid.");
            break;
      }
    }
  }
//    if (token.compare("B")) {
//      brght = atoi(&command[1]);
//      brght = check(brght, 10, 100);
//    }
//    else if (token.compare("F")) {
//      freq = atoi(&command[1]);
//      freq = check(freq, 10, 10000);
//    }
//    else if (token.compare("N")) {
//      num = atoi(&command[1]);
//      num = check(num, 1, NUM_LEDS);
//    }
//    else {
//      Serial.print("ERROR: Command: '");
//      Serial.print(command);
//      Serial.println("' is not valid."); 
//    }

  // Final set feedback.
  Serial.print("Num: ");
  Serial.println(num);
  Serial.print("Brght: ");
  Serial.println(brght);
  Serial.print("Freq.: ");
  Serial.println(freq);
  Serial.print("Palette.: ");
  Serial.println(palette);
}

int check(int var, int min, int max) {
  if (var < min) {
    var = min;
  }
  if (var > max) {
    var = max;
  }
  return var;
}
