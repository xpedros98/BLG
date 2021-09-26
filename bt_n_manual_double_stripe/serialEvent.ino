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
  switch (command[0]) {
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
                // Find the first delimited word.
                token = strtok(command, delimiter);
                
                tokens_counter = 0;
                // Find the following delimited words.
                while(token != NULL) {
                  Serial.println(token);
                  tokens_counter += 1;
                  token = strtok(NULL, delimiter);
                  
                //   if (counter == 1) {
                //     palette = token.toInt();
                //     palette = check(palette, 0, 10); // The length of the palettes array should be checkd here.
                //   }
                //   else if (counter == 2) {
                //     R = token.toInt();
                //     R = check(R, 0, 255);
                //   }
                //   else if (counter == 3) {
                //     G = token.toInt();
                //     G = check(G, 0, 255);
                //   }
                //   else if (counter == 4) {
                //     B = token.toInt();
                //     B = check(B, 0, 255);
                //   }
                }
        break;
      default:
                Serial.print("ERROR: Command: '");
                Serial.print(command);
                Serial.println("' is not valid."); 
        break;
  }
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
