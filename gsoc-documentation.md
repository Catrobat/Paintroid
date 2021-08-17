# GSoC documentation Gerhard Fromm

As part of Summer of Code my project was to further expand the brush tool in Paintroid.
This was the initial plan. However these plans quickly changed when we discussed it while implementing the different tasks.
The new goal was to implement some advanced settings which effect multiple tools and extend the tool menu with 1 or 2 more tools.

## New additions:
<<<<<<< HEAD
New advanced settings: 

![Concept](https://i.imgur.com/SFo5C0B.png)
![Concept](https://i.imgur.com/rUOgVoU.png)

New Tool:

![Concept](https://i.imgur.com/PLrNj4l.png)
=======
![Concept] (https://imgur.com/SFo5C0B)

![Concept] (https://imgur.com/rUOgVoU)

![Concept] (https://imgur.com/PLrNj4l)
>>>>>>> Update and rename gsoc-documentation to gsoc-documentation.md


# GSoC Tasks

## 1. Adding advanced setting: Antialiasing
Previously antialiasing was automatically on. The task was to 
implement a new option to switch it on and off via a new menu entry.
https://github.com/Catrobat/Paintroid/pull/957

## 2. Adding advanved setting: Smoothing
Added an basic algorithm which smoothes the drawing line the user
<<<<<<< HEAD
draws when a certain speed is matched. Can also be turned on and off.

### Functionality:
Before smoothing was implemented the approach to drawing a line was to draw a quadradic bezier curve
between 2 points. Because it only took 2 points into account it did not always look smooth. With the 
new approach points on the drawing surface got recalculated (cubic splines) and then instead of 2 points,
3 points were taken to draw a cubic bezier curve. Although this approach is not perfect it got
a decent result. Also to not hinder the user when trying to draw slow and exact, a certain speed needs to be matched.
https://github.com/Catrobat/Paintroid/pull/969

## 3. New Tool: Watercolor brush
Implemented a new tool which mimics a watercolor brush.
Also changed some functioning how the colorpicker works.

### Functionality:
To mimic a watercolor effect an already existing maskfilter was used to implement it.
The maskfilter was simply put onto the paint and we got a good result. Since there was 
an additional option for the strength of the watercolor brush I put a slider into the colorpicker as seen below.
https://github.com/Catrobat/Paintroid/pull/1000

Slider:

![Concept](https://i.imgur.com/M6KAko8.png)

Watercolor vs Brush

![Concept](https://i.imgur.com/Zae42Gc.png)
![Concept](https://i.imgur.com/C7BMc53.png)


------------------------------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------------------------
=======
draws when a certain speed is matched.
https://github.com/Catrobat/Paintroid/pull/969

## 3. New Tool: Watercolor brush
Implemented a new Tool which mimics a watercolor brush.
Also changed some functioning how the colorpicker works.
https://github.com/Catrobat/Paintroid/pull/1000


>>>>>>> Update and rename gsoc-documentation to gsoc-documentation.md
# Unfinished tasks:
Due to time limitation this task was sadly not possible to complete.

## New Tool: Smudge Tool
<<<<<<< HEAD
The goal was to implement a tool that smudges colors. 

### Problems: 
The problem of this tool was the efficiency. After you draw for a certain period of time with this tool
it gets slower and slower. This was due to implementation. The tool always compares 2 bitmaps and merges them 
with an algorithm. The new bitmap gets then drawn onto the surface. This repeats until the user has finished drawing on the screen.
This however creates way too many bitmaps which will lag the app after a while.

https://github.com/Catrobat/Paintroid/pull/1004

**some screenshots from the tool:**

Before smudge tool vs after smudge tool

![Concept](https://i.imgur.com/Hr5IvZW.png)
![Concept](https://i.imgur.com/ZbSLbdn.png)

=======
https://github.com/Catrobat/Paintroid/pull/1004

>>>>>>> Update and rename gsoc-documentation to gsoc-documentation.md

