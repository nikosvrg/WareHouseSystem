from django.views.generic import TemplateView
from django.contrib import messages
from django.contrib.auth.mixins import LoginRequiredMixin
from django.utils.decorators import method_decorator
from django.forms.utils import ValidationError
from django.views.generic import ListView
from django.shortcuts import render,redirect,get_object_or_404, Http404
from django.http import HttpResponse, request
from django.db.models import Count
from .forms import *
from .models import *




def homepage(request):
    context = {

    }
    return render(request, 'HistoryMuseum/homepage.html', context)


class HeroesPageView(LoginRequiredMixin,TemplateView):
    template_name = 'HistoryMuseum/hero.html'

    def get(self, request, *args, **kwargs):
        user = request.user

        heroes = Hero.objects.all()

        context = {
            'heroes': heroes
        }

        return render(request, self.template_name, context)


class HeroRedirectView(LoginRequiredMixin, TemplateView):

    def get(self, request, *args, **kwargs):
        user = request.user

        return redirect(f'/HistoryMuseum/hero')


class HeroTheoryView(LoginRequiredMixin, TemplateView):
    template_name = 'HistoryMuseum/theory.html'

    def get(self, request, hero_id, *args, **kwargs):
        user = request.user

        try:
            hero = Hero.objects.get(id=hero_id)
        except:
            raise Http404('Hero does not exist')

        if hero_id is None:
            hero_id = user.student.hero_studying

        if hero_id > user.student.hero_studying:
            return redirect(f'/HistoryMuseum/theory/{user.student.hero_studying}/')

        try:
            t = Test.objects.get(id=hero_id)
            stats, created = Statistics.get_or_create(user=user, test=t)
        except:
            raise Http404('Hero should be followed by Test')

        stats.times_read += 1
        stats.save()
        context = {
            'hero' : hero,
            'hero_id':hero_id
        }

        return render(request, self.template_name, context)


class TestRedirectView(LoginRequiredMixin, TemplateView):

    def get(self, request, *args, **kwargs):
        user = request.user
        return redirect(f'/HistoryMuseum/Test/{user.student.test_taking}/')



class TestPageView(LoginRequiredMixin, TemplateView):
    template_name = 'HistoryMuseum/Test.html'


    def get(self, request, id, *args, **kwargs):
        user = request.user

        if id is None:
            id = user.student.hero_studying

        if id > user.student.hero_studying:
            return redirect(f'/HistoryMuseum/Test/{user.student.test_taking}/')

        try:
            t = Test.objects.get(id=id)

        except:
            return redirect('/HistoryMuseum/completed')

        limit = Question.objects.filter(test=t, show=True).count()

        form = TestForm(hero=id,limit=limit)

        context = {
            'form' : form,
            'test':t
        }
        return render(request, self.template_name, context)

# def hero_theory(request, id):

#     hero = get_object_or_404(Hero, id=id)
#     herotheory = Hero.objects.filter(pk=id)
#     context ={
#         'heroes': hero,
#         'herotheory': herotheory
#     }
#     return render(request, 'HistoryMuseum/theory.html', context)


def post(self, request, id, *args, **kwargs):
    user = request
    t = Test.objects.get(id=id)
    limit = Question.objects.filter(test=t, show=True).count()
    form = TestForm(request.POST, hero=id, limit=limit)

    if form.is_valid():

        stats, created = Statistics.object.get_or_create(user=user, test= t)
        data = form.cleaned_data


        current_correct_answers = 0

        for i in range(limit):

            user_answer = data.get(f'question{i}')
            stats_hero, created = StatisticsPerHero.objects.get_or_create(user=user, hero=user_answer.question.hero)

            if user_answer.correct:
                current_correct_answers +=1
                stats_hero.answers_correct +=1

            else:
                stats_hero.answers_wrong += 1

                stats_hero.answers_total +=1

            if stats_hero.answers_wrong >= stats.hero.answers_correct:
                stats_hero.bad_at = True
            
            else:
                stats_hero.bad_at = False

            stats_hero.save()

        stats.answers_correct += current_correct_answers
        stats.answers_wrong += limit - current_correct_answers
        stats.answers_total += limit

        stats.times_taken += 1
        stats.success_rate = round(stats.answers_correct / stats.answers_total, 2 )*100

        if current_correct_answers / limit >= 0.6:
            stats.passed = True

            if user.student.hero_studying == id:
                user.student.hero_studying +=1
                user.profile.test_taking +=1

            user.save()
            stats.save()

        else:
            user.save()
            stats.save()
            return redirect(f'/HistoryMuseum/hero/{id}')

        
        try:
            next_hero = Hero.objects.get(id=id+1)

        except:

            try: 
                next_t = Test.objects.get(id=id+1)
            except:
                return redirect('/HistoryMuseum/completed')
            return redirect(f'/HistoryMuseum/Test/{id+1}/')

        return redirect(f'/HistoryMuseum/hero/{id+1}/')     

    context = {
        'form': form,
        'test': test,

    }   

    return render(request, self.template_name, context)


class TestsCompletedView(LoginRequiredMixin, TemplateView):

    template_name = 'HistoryMuseum/completed.html'

    def get(self, request, *args, **kwargs):
        user = request.user

        context = {}

        return render(request, self.template_name, context)            