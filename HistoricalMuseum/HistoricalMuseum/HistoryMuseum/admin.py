from django.contrib import admin
from nested_admin.nested import NestedStackedInline, NestedModelAdmin, NestedTabularInline

from .models import Hero, Test, Question, Answer, Statistics, StatisticsPerHero
# Register your models here.

class AnswerInLine(NestedTabularInline):
    model = Answer
    extra = 4
    max_num = 4

class QuestionInline(NestedTabularInline):
    model = Question
    inlines = [AnswerInLine,]
    extra = 1


class TestAdmin(NestedModelAdmin):
    inlines = [QuestionInline]



admin.site.register(Statistics)
admin.site.register(StatisticsPerHero)

admin.site.register(Hero)
admin.site.register(Test, TestAdmin)
admin.site.register(Question)
admin.site.register(Answer)







