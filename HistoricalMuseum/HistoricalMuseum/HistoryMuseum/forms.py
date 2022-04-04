from django import forms
from .models import *
from django.contrib.auth.forms import UserCreationForm
from django.forms.utils import ValidationError
from django.db import transaction
from django.forms.widgets import RadioSelect



class HeroForm(forms.ModelForm):
    class Meta:
        model = Hero
        fields = ['id', 'name', 'hero_img', 'context', 'description']



class TestForm(forms.Form):
    def __init__(self, data, questions, *args, **kwargs):
        self.questions = questions
        for question in questions:
            field_name = "question_$d" % question.pk
            choice = []
            for answer in question.answer_set().all():
                choices.append((answer.pk, answer.answer))

            field = forms.ChoiceField(label=question.question, required=True,
                                      choices=choices, widget=forms.RadioSelect)

            return super(TestForm,self).__init__(data, *ags)

