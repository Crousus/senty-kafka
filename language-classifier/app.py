import uvicorn
from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline, BertTokenizer, BertForSequenceClassification, TextClassificationPipeline

print("Loading the model...")

app = FastAPI()

# Load the model
model_directory = "jb2k/bert-base-multilingual-cased-language-detection"

# Load the tokenizer and the model
tokenizer = BertTokenizer.from_pretrained(model_directory)
model = BertForSequenceClassification.from_pretrained(model_directory)

# Create a pipeline
classifier = TextClassificationPipeline(model=model, tokenizer=tokenizer)

class Item(BaseModel):
    text: str

label_mapping = {
    "LABEL_0": "ar",
    "LABEL_1": "eu",
    "LABEL_2": "br",
    "LABEL_3": "ca",
    "LABEL_4": "zh",
    "LABEL_5": "zh",
    "LABEL_6": "zh",
    "LABEL_7": "cv",
    "LABEL_8": "cs",
    "LABEL_9": "dv",
    "LABEL_10": "nl",
    "LABEL_11": "en",
    "LABEL_12": "eo",
    "LABEL_13": "et",
    "LABEL_14": "fr",
    "LABEL_15": "fy",
    "LABEL_16": "ka",
    "LABEL_17": "de",
    "LABEL_18": "el",
    "LABEL_19": "cnh",
    "LABEL_20": "id",
    "LABEL_21": "ia",
    "LABEL_22": "it",
    "LABEL_23": "ja",
    "LABEL_24": "kab",
    "LABEL_25": "rw",
    "LABEL_26": "ky",
    "LABEL_27": "lv",
    "LABEL_28": "mt",
    "LABEL_29": "mn",
    "LABEL_30": "fa",
    "LABEL_31": "pl",
    "LABEL_32": "pt",
    "LABEL_33": "ro",
    "LABEL_34": "rm",
    "LABEL_35": "ru",
    "LABEL_36": "sah",
    "LABEL_37": "sl",
    "LABEL_38": "es",
    "LABEL_39": "sv",
    "LABEL_40": "ta",
    "LABEL_41": "tt",
    "LABEL_42": "tr",
    "LABEL_43": "uk",
    "LABEL_44": "cy",
}

@app.post("/predict")
async def predict(item: Item):
    result = classifier(item.text)
    # Map the label ID to the corresponding language
    language = label_mapping.get(result[0]['label'], "Unknown")
    return {"language": language}

if __name__ == "__main__":
    uvicorn.run(app, host="localhost", port=2000)
