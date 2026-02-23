from fastapi import FastAPI
from pydantic import BaseModel
import torch
from transformers import PreTrainedTokenizerFast, BartForConditionalGeneration

app = FastAPI()

tokenizer = PreTrainedTokenizerFast.from_pretrained("digit82/kobart-summarization")
model = BartForConditionalGeneration.from_pretrained("digit82/kobart-summarization")

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)

class SummaryRequest(BaseModel):
    text: str

@app.post("/summarize")
def summarize(req: SummaryRequest):
    text = req.text.replace("\n", " ")
    inputs = tokenizer(
            text,
            return_tensors="pt",
            truncation=True,
            max_length=1024
        ).to(device)

    summary_ids = model.generate(
            inputs["input_ids"],
            max_length=60,
            min_length=20,
            num_beams=4,
            length_penalty=1.2,
            repetition_penalty=2.0,
            no_repeat_ngram_size=3,
            early_stopping=True
        )

    summary_text = tokenizer.decode(summary_ids[0], skip_special_tokens=True)

    return {"summary": summary_text}
